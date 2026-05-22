# REST And WebClient

## Sources To Check

- Notes: `08-05-26-REST.md`, `Project.md`.
- Primary exercise for this project: `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_REST\Spring_Boot_rest_fruit_start`.
- Secondary exercises only when the fruit exercise does not answer the question: `EWDJ_REST/Spring_Boot_rest_example`, `Spring_Boot_rest_example2`.
- Slides: `webservices_REST.pdf`, `Oneindige_lus_vermijden.pdf`.

## Required Exercise Pattern

When the REST block for `call-the-match` starts, copy the structure and style of the fruit exercise as closely as the project domain allows.

Use these files as the good examples:

- REST controller shape: `Spring_Boot_rest_fruit_start/src/main/java/com/example/spring_boot_rest_fruit_start/controller/FruitRestController.java`.
- Error advice shape: `Spring_Boot_rest_fruit_start/src/main/java/com/example/spring_boot_rest_fruit_start/advice/FruitErrorAdvice.java`.
- Error response record: `Spring_Boot_rest_fruit_start/src/main/java/com/example/spring_boot_rest_fruit_start/dto/response/ErrorResponse.java`.
- Client runner: `Spring_Boot_rest_fruit_start/src/main/java/com/example/spring_boot_rest_fruit_start/client/ClientRunner.java`.
- WebClient demo: `Spring_Boot_rest_fruit_start/src/main/java/com/example/spring_boot_rest_fruit_start/client/RestClientDemo.java`.
- REST controller tests: `Spring_Boot_rest_fruit_start/src/test/java/com/example/spring_boot_rest_fruit_start/controller/FruitRestControllerTest.java`.

Do not replace this with a generic internet REST architecture when implementing this school project.

## call-the-match REST Scope

Keep REST/WebClient as a separate late feature block.

Recommended endpoints:

- `GET /api/competitions?date=YYYY-MM-DD`: returns matches for a date.
- `GET /api/stadiums/{id}/capacity`: returns stadium capacity.

Permit public GET REST endpoints in security config.

## REST Controller Pattern

Use the fruit exercise controller structure:

- `@RestController`, not a normal `@Controller`.
- `@RequiredArgsConstructor`.
- class-level `@RequestMapping(value = "/...")`.
- `@GetMapping("/{id}")` for detail and `@GetMapping` for list retrieval when that matches the assignment.
- controller delegates to a service and returns REST data directly, never a Thymeleaf view.
- use the same concise method flow as the exercise: service call in the controller method, not controller-side business logic.

Good shape from the fruit exercise, adapted only in domain names:

```java
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/competitions")
public class CompetitionRestController {
    private final CompetitionService competitionService;

    @GetMapping("/{id}")
    public Competition getCompetitionDetail(@PathVariable("id") int competitionId) {
        return competitionService.getCompetitionDetail(competitionId);
    }

    @GetMapping
    public List<Competition> getAllCompetitions() {
        return competitionService.getAllCompetitions();
    }
}
```

The fruit exercise returns its REST model directly. For `call-the-match`, keep that school style when the returned object is safe and serializes cleanly. Use a response DTO when an entity relationship would cause a JSON loop, expose fields that should not leave the API, or the existing assignment design already uses a DTO boundary.

## REST Error Advice

Use the fruit exercise error shape: a `@RestControllerAdvice` with a typed `@ExceptionHandler`, `@ResponseStatus`, and a small `ErrorResponse` record with `status`, `message`, and `timestamp`.

```java
@RestControllerAdvice
class CompetitionErrorAdvice {
    @ExceptionHandler(CompetitionNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse competitionNotFoundHandler(CompetitionNotFound ex) {
        return new ErrorResponse(
                404,
                ex.getMessage(),
                LocalDateTime.now().toString()
        );
    }
}
```

## WebClient Pattern

Use the two-file client structure from the fruit exercise:

- `ClientRunner` contains the `main` method and creates the demo client.
- `RestClientDemo` owns the `WebClient`, calls detail/list endpoints, prints results, and demonstrates error handling for a not-found call.

Good runner shape:

```java
public class ClientRunner {
    public static void main(String[] args) throws Exception {
        new RestClientDemo();
    }
}
```

Good WebClient shape from the exercise, adapted only in domain names:

```java
private final String SERVER_URI = "http://localhost:8080/competitions";

private final WebClient webClient = WebClient.builder()
        .baseUrl(SERVER_URI)
        .build();

private Flux<Competition> getAllCompetitions() {
    return webClient.get()
            .retrieve()
            .bodyToFlux(Competition.class);
}

private Mono<Competition> getCompetition(int id) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/{id}").build(id))
            .retrieve()
            .bodyToMono(Competition.class);
}
```

The demo constructor may use `doOnNext(...).block()`, `blockLast()`, and a not-found path with `doOnError(...)`, `onErrorResume(...)`, and `Mono.empty()` like the fruit exercise.

## REST Test Pattern

Use `FruitRestControllerTest` as the good REST test example.

Expected shape:

- `@WebMvcTest(TheRestController.class)`.
- autowired `MockMvc`.
- `@MockitoBean` for the service dependency.
- `mockMvc.perform(get(...))`.
- assert HTTP status and JSON body with `jsonPath`.
- verify service calls with `Mockito.verify`.
- test detail GET success.
- test not-found REST advice response with JSON fields `status`, `message`, and `timestamp`.
- test list GET for an empty result and a non-empty result.

Good structure, adapted only in domain names:

```java
@WebMvcTest(CompetitionRestController.class)
class CompetitionRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompetitionService mock;

    @Test
    void testGetCompetition_notFound() throws Exception {
        Mockito.when(mock.getCompetitionDetail(ID)).thenThrow(new CompetitionNotFound(ID));

        mockMvc.perform(get("/competitions/" + ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        Mockito.verify(mock).getCompetitionDetail(ID);
    }
}
```

## JSON Loop Rule

For bidirectional JPA relationships, avoid infinite JSON loops. Choose a clear boundary using DTOs first. If serializing entities is unavoidable, use `@JsonIgnore` or managed/back references deliberately.

For this project, prefer REST response DTOs to avoid entity graph loops.
