param(
    [string]$Feature = "",
    [switch]$Hitl,
    [switch]$Afk,
    [int]$Tasks = 1,
    [switch]$Commit
)

$ErrorActionPreference = "Stop"

if ($Hitl -and $Afk) {
    Write-Host "Choose either -Hitl or -Afk, not both."
    exit 1
}

if ($Tasks -lt 1) {
    Write-Host "-Tasks must be at least 1."
    exit 1
}

$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

if (-not $Feature) {
    $plans = @(Get-ChildItem -Path "src/main/resources/plan" -Filter "plan.json" -Recurse -ErrorAction SilentlyContinue)
    if ($plans.Count -eq 1) {
        $Feature = $plans[0].Directory.Name
    } elseif ($plans.Count -gt 1) {
        Write-Host "Multiple feature plans found. Re-run with -Feature {name}."
        $plans | ForEach-Object { Write-Host " - $($_.Directory.Name)" }
        exit 1
    } else {
        Write-Host "No plan.json found. Run prd-to-plan-json first."
        exit 1
    }
}

$prdPath = "src/main/resources/prd/$Feature-prd.md"
$planMdPath = "src/main/resources/prd/$Feature/plan.md"
$planJsonPath = "src/main/resources/plan/$Feature/plan.json"
$ralphDir = "src/main/resources/ralph/$Feature"
$progressPath = "$ralphDir/progress.md"
$todoPath = "$ralphDir/TODO.md"

if (-not (Test-Path $planJsonPath)) {
    Write-Host "Missing plan.json: $planJsonPath"
    Write-Host "Run prd-to-plan-json before ralph-init/ralph."
    exit 1
}

New-Item -ItemType Directory -Force -Path $ralphDir | Out-Null
if (-not (Test-Path $progressPath)) {
    @"
# Ralph Progress Log: $Feature

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries
"@ | Set-Content -Path $progressPath
}
if (-not (Test-Path $todoPath)) {
    @"
# Ralph TODO: $Feature

Human review items, blockers, and deferred decisions.

## Open Items
"@ | Set-Content -Path $todoPath
}

$plan = Get-Content $planJsonPath -Raw | ConvertFrom-Json
$openTasks = @($plan.tasks | Where-Object { $_.passes -eq $false } | Sort-Object priority)
$taskLimit = if ($Afk) { $Tasks } else { 1 }
$mode = if ($Hitl) { "HITL" } elseif ($Afk) { "AFK" } else { "preview" }
$promptFile = if ($Hitl) {
    "prompts/ralph-iteration-hitl.md"
} elseif ($Afk) {
    "prompts/ralph-iteration-afk.md"
} else {
    "prompts/ralph-iteration.md"
}

Write-Host "Ralph context ready"
Write-Host "Feature: $Feature"
Write-Host "PRD: $prdPath"
Write-Host "Plan.md: $planMdPath"
Write-Host "Plan.json: $planJsonPath"
Write-Host "Open tasks: $($openTasks.Count)"
Write-Host "Progress: $progressPath"
Write-Host "TODO: $todoPath"
Write-Host "Prompt: $promptFile"

if (-not (Test-Path $prdPath)) {
    Write-Host "Warning: PRD path is missing: $prdPath"
}
if (-not (Test-Path $planMdPath)) {
    Write-Host "Warning: plan.md path is missing: $planMdPath"
}

Write-Host ""
if ($mode -eq "preview") {
    Write-Host "Preview only. Recommended first run:"
    Write-Host ".\scripts\ralph.ps1 -Feature $Feature -Hitl"
    Write-Host ""
    Write-Host "When the workflow is trusted:"
    Write-Host ".\scripts\ralph.ps1 -Feature $Feature -Afk -Tasks 3"
}

Write-Host ""
Write-Host "Paste this into Codex:"
Write-Host "Use the Ralph $mode workflow for feature '$Feature'. Task limit: $taskLimit. Read @$prdPath @$planMdPath @$planJsonPath @$progressPath @$todoPath and @$promptFile, follow ewd-schoolrichtlijnen, check git status --short, then work the highest-priority incomplete task(s). Commit only if Commit=$Commit."
