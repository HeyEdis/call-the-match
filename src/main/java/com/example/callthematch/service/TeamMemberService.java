package com.example.callthematch.service;

import com.example.callthematch.model.Competition;
import com.example.callthematch.model.Prediction;
import com.example.callthematch.model.Team;
import com.example.callthematch.model.TeamMember;
import com.example.callthematch.repository.PredictionRepository;
import com.example.callthematch.repository.TeamMemberRepository;
import com.example.callthematch.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final PredictionRepository predictionRepository;
    private final ScoringService scoringService;

    @Transactional
    public void recalculateScoresAfterResult(Competition competition) {
        if (competition.getScoreA() == null || competition.getScoreB() == null) {
            return;
        }

        List<Prediction> competitionPredictions = predictionRepository.findAllByCompetition(competition);
        competitionPredictions.forEach(prediction -> prediction.setPointsEarned(
                scoringService.calculateBasePoints(prediction, competition.getScoreA(), competition.getScoreB())));
        predictionRepository.saveAll(competitionPredictions);

        findAffectedTeams(competitionPredictions).forEach(this::recalculateTeamScores);
    }

    private Set<Team> findAffectedTeams(List<Prediction> predictions) {
        Set<Team> teams = new HashSet<>();
        predictions.stream()
                .map(Prediction::getUser)
                .filter(Objects::nonNull)
                .forEach(user -> teamMemberRepository.findAllByUserId(user.getId()).stream()
                        .map(TeamMember::getTeam)
                        .forEach(teams::add));
        return teams;
    }

    private void recalculateTeamScores(Team team) {
        List<TeamMember> members = teamMemberRepository.findAllByTeam(team);
        members.forEach(member -> member.setScore(recalculateMemberScore(member, members)));
        teamMemberRepository.saveAll(members);

        team.setScore(members.stream()
                .map(TeamMember::getScore)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum());
        teamRepository.save(team);
    }

    private int recalculateMemberScore(TeamMember member, List<TeamMember> teamMembers) {
        return predictionRepository.findAllByUser(member.getUser()).stream()
                .filter(prediction -> prediction.getCompetition().getScoreA() != null)
                .filter(prediction -> prediction.getCompetition().getScoreB() != null)
                .mapToInt(prediction -> scoringService.calculatePoints(
                        prediction,
                        prediction.getCompetition().getScoreA(),
                        prediction.getCompetition().getScoreB(),
                        teamPredictionsFor(prediction.getCompetition(), teamMembers)))
                .sum();
    }

    private List<Prediction> teamPredictionsFor(Competition competition, List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getUser)
                .map(user -> predictionRepository.findByUserAndCompetition(user, competition))
                .flatMap(Optional::stream)
                .toList();
    }


}
