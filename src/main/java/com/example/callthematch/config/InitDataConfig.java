package com.example.callthematch.config;

import com.example.callthematch.model.*;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.CountryRepository;
import com.example.callthematch.repository.LocationRepository;
import com.example.callthematch.repository.PredictionRepository;
import com.example.callthematch.repository.RankingRepository;
import com.example.callthematch.repository.StadiumRepository;
import com.example.callthematch.repository.TeamMemberRepository;
import com.example.callthematch.repository.TeamRepository;
import com.example.callthematch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Profile("dev") // laten weten enkel in development
public class InitDataConfig implements CommandLineRunner {

    private final CompetitionRepository competitionRepository;
    private final CountryRepository countryRepository;
    private final LocationRepository locationRepository;
    private final PredictionRepository predictionRepository;
    private final RankingRepository rankingRepository;
    private final StadiumRepository stadiumRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    Faker faker = new Faker(Locale.of("nl", "BE"));
    Random r = new Random();

    @Override
    public void run(String... args) throws Exception {

        User admin = User.builder()
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .userName("Lord of Lords")
                .passwordHash("password")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var generatedUsers = new ArrayList<User>();

        for (int i = 0; i < 30; i++) {
            String username = faker.animal().name().replace(" ", "")
                    + faker.number().numberBetween(1, 999);
            User user = User.builder()
                    .email(faker.internet().emailAddress())
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .userName(username)
                    .passwordHash("password")
                    .role(Role.USER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            generatedUsers.add(user);
        }

        userRepository.save(admin);
        userRepository.saveAll(generatedUsers);

        // ── 2. COUNTRIES ──────────────────────────────────────────────────────
        Country usa = Country.builder().landCode(1).name("United States").build();
        Country mexico = Country.builder().landCode(52).name("Mexico").build();
        Country canada = Country.builder().landCode(1).name("Canada").build();
        Country japan = Country.builder().landCode(81).name("Japan").build();
        Country iran = Country.builder().landCode(98).name("Iran").build();
        Country southKorea = Country.builder().landCode(82).name("South Korea").build();
        Country australia = Country.builder().landCode(61).name("Australia").build();
        Country saudiArabia = Country.builder().landCode(966).name("Saudi Arabia").build();
        Country qatar = Country.builder().landCode(974).name("Qatar").build();
        Country uzbekistan = Country.builder().landCode(998).name("Uzbekistan").build();
        Country jordan = Country.builder().landCode(962).name("Jordan").build();
        Country iraq = Country.builder().landCode(964).name("Iraq").build();
        Country argentina = Country.builder().landCode(54).name("Argentina").build();
        Country brazil = Country.builder().landCode(55).name("Brazil").build();
        Country uruguay = Country.builder().landCode(598).name("Uruguay").build();
        Country colombia = Country.builder().landCode(57).name("Colombia").build();
        Country ecuador = Country.builder().landCode(593).name("Ecuador").build();
        Country paraguay = Country.builder().landCode(595).name("Paraguay").build();
        Country newZealand = Country.builder().landCode(64).name("New Zealand").build();
        Country morocco = Country.builder().landCode(212).name("Morocco").build();
        Country senegal = Country.builder().landCode(221).name("Senegal").build();
        Country egypt = Country.builder().landCode(20).name("Egypt").build();
        Country algeria = Country.builder().landCode(213).name("Algeria").build();
        Country tunisia = Country.builder().landCode(216).name("Tunisia").build();
        Country southAfrica = Country.builder().landCode(27).name("South Africa").build();
        Country ivoryCoast = Country.builder().landCode(225).name("Ivory Coast").build();
        Country ghana = Country.builder().landCode(233).name("Ghana").build();
        Country capeVerde = Country.builder().landCode(238).name("Cape Verde").build();
        Country drCongo = Country.builder().landCode(243).name("DR Congo").build();
        Country england = Country.builder().landCode(44).name("England").build();
        Country france = Country.builder().landCode(33).name("France").build();
        Country spain = Country.builder().landCode(34).name("Spain").build();
        Country germany = Country.builder().landCode(49).name("Germany").build();
        Country portugal = Country.builder().landCode(351).name("Portugal").build();
        Country netherlands = Country.builder().landCode(31).name("Netherlands").build();
        Country belgium = Country.builder().landCode(32).name("Belgium").build();
        Country croatia = Country.builder().landCode(385).name("Croatia").build();
        Country switzerland = Country.builder().landCode(41).name("Switzerland").build();
        Country austria = Country.builder().landCode(43).name("Austria").build();
        Country scotland = Country.builder().landCode(44).name("Scotland").build();
        Country norway = Country.builder().landCode(47).name("Norway").build();
        Country bosnia = Country.builder().landCode(387).name("Bosnia and Herzegovina").build();
        Country sweden = Country.builder().landCode(46).name("Sweden").build();
        Country turkey = Country.builder().landCode(90).name("Turkey").build();
        Country czechia = Country.builder().landCode(420).name("Czechia").build();
        Country panama = Country.builder().landCode(507).name("Panama").build();
        Country curacao = Country.builder().landCode(599).name("Curacao").build();
        Country haiti = Country.builder().landCode(509).name("Haiti").build();

        countryRepository.saveAll(List.of(
                usa, mexico, canada, japan, iran, southKorea, australia, saudiArabia,
                qatar, uzbekistan, jordan, iraq, argentina, brazil, uruguay, colombia,
                ecuador, paraguay, newZealand, morocco, senegal, egypt, algeria, tunisia,
                southAfrica, ivoryCoast, ghana, capeVerde, drCongo, england, france,
                spain, germany, portugal, netherlands, belgium, croatia, switzerland,
                austria, scotland, norway, bosnia, sweden, turkey, czechia, panama,
                curacao, haiti
        ));

        // ── 5. LOCATIONS ──────────────────────────────────────────────────────
        Location newYork = Location.builder().city("New York").build();
        Location dallas = Location.builder().city("Dallas").build();
        Location atlanta = Location.builder().city("Atlanta").build();
        Location losAngeles = Location.builder().city("Los Angeles").build();
        Location kansasCity = Location.builder().city("Kansas City").build();
        Location houston = Location.builder().city("Houston").build();
        Location sanFran = Location.builder().city("San Francisco").build();
        Location seattle = Location.builder().city("Seattle").build();
        Location philadelphia = Location.builder().city("Philadelphia").build();
        Location miami = Location.builder().city("Miami").build();
        Location boston = Location.builder().city("Boston").build();
        Location mexicoCity = Location.builder().city("Mexico City").build();
        Location monterrey = Location.builder().city("Monterrey").build();
        Location guadalajara = Location.builder().city("Guadalajara").build();
        Location toronto = Location.builder().city("Toronto").build();
        Location vancouver = Location.builder().city("Vancouver").build();

        locationRepository.saveAll(List.of(
                newYork, losAngeles, dallas, sanFran, miami, seattle,
                boston, houston, atlanta, kansasCity, mexicoCity,
                guadalajara, monterrey, toronto, vancouver, philadelphia
        ));

        // ── 6. STADIUMS ───────────────────────────────────────────────────────
        Stadium metLife = Stadium.builder().location(newYork).name("MetLife Stadium").code(1001).capacity(82500).build();
        Stadium attStadium = Stadium.builder().location(dallas).name("AT&T Stadium").code(1003).capacity(80000).build();
        Stadium mercedesBenz = Stadium.builder().location(atlanta).name("Mercedes-Benz Stadium").code(1009).capacity(71000).build();
        Stadium sofi = Stadium.builder().location(losAngeles).name("SoFi Stadium").code(1002).capacity(70240).build();
        Stadium levisStad = Stadium.builder().location(sanFran).name("Levi's Stadium").code(1004).capacity(68500).build();
        Stadium hardRock = Stadium.builder().location(miami).name("Hard Rock Stadium").code(1005).capacity(65326).build();
        Stadium lumen = Stadium.builder().location(seattle).name("Lumen Field").code(1006).capacity(69000).build();
        Stadium gillette = Stadium.builder().location(boston).name("Gillette Stadium").code(1007).capacity(65878).build();
        Stadium nrg = Stadium.builder().location(houston).name("NRG Stadium").code(1008).capacity(72220).build();
        Stadium arrowhead = Stadium.builder().location(kansasCity).name("Arrowhead Stadium").code(1010).capacity(76416).build();
        Stadium azteca = Stadium.builder().location(mexicoCity).name("Estadio Azteca").code(1011).capacity(87523).build();
        Stadium akron = Stadium.builder().location(guadalajara).name("Estadio Akron").code(1012).capacity(49850).build();
        Stadium bbva = Stadium.builder().location(monterrey).name("Estadio BBVA").code(1013).capacity(53500).build();
        Stadium bmo = Stadium.builder().location(toronto).name("BMO Field").code(1014).capacity(45736).build();
        Stadium bcPlace = Stadium.builder().location(vancouver).name("BC Place").code(1015).capacity(54500).build();
        Stadium lincolnFinancial = Stadium.builder().location(philadelphia).name("Lincoln Financial Field").code(1016).capacity(69000).build();

        stadiumRepository.saveAll(List.of(
                metLife, sofi, attStadium, levisStad, hardRock, lumen,
                gillette, nrg, mercedesBenz, arrowhead, azteca, akron,
                bbva, bmo, bcPlace, lincolnFinancial
        ));

        // ── 7. TEAMS ───────────────────────────────────────────
        var generatedTeams = new ArrayList<Team>();
        for (int i = 0; i < 6; i++) {
            Team team = Team.builder()
                    .name(faker.team().name())
                    .inviteCode(faker.bothify("????####").toUpperCase())
                    .owner(generatedUsers.get(i))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            generatedTeams.add(team);
        }

        teamRepository.saveAll(generatedTeams);

        // ── 8. TEAM MEMBERS ───────────────────────────────────────────────────
        var generatedTeamMembers = new ArrayList<TeamMember>();
        int userIndex = generatedTeams.size();
        for (Team team : generatedTeams) {
            for (int i = 0; i < 4; i++) {
                TeamMember teamMember = TeamMember.builder()
                        .user(generatedUsers.get(userIndex))
                        .team(team)
                        .role(TeamRole.MEMBER)
                        .score(faker.number().randomDigitNotZero())
                        .joinedAt(LocalDateTime.now())
                        .build();
                generatedTeamMembers.add(teamMember);
                userIndex++;
            }
        }

        teamMemberRepository.saveAll(generatedTeamMembers);

        // ADDING OWNERS TO TEAM
        var ownerMembers = new ArrayList<TeamMember>();
        for (int i = 0; i < generatedTeams.size(); i++) {
            TeamMember ownerMember = TeamMember.builder()
                    .user(generatedUsers.get(i))
                    .team(generatedTeams.get(i))
                    .role(TeamRole.OWNER)
                    .score(faker.number().randomDigitNotZero())
                    .joinedAt(LocalDateTime.now())
                    .build();
            ownerMembers.add(ownerMember);
        }
        teamMemberRepository.saveAll(ownerMembers);

        // ── 10. COMPETITIONS ─────────────────────
        Competition c1 = Competition.builder()
                .teamA(bosnia).teamB(canada)
                .stadium(attStadium)
                .date(LocalDate.of(2026, 5, 12))
                .time(LocalTime.of(13 + (r.nextInt(9)), 0))
                .scoreA(3)
                .scoreB(1)
                .createdAt(LocalDateTime.now())
                .build();

        Competition c2 = Competition.builder()
                .teamA(qatar).teamB(switzerland)
                .stadium(azteca)
                .date(LocalDate.of(2026, 5, 18))
                .time(LocalTime.of(13 + (r.nextInt(9)), 0))
                .scoreA(2)
                .scoreB(2)
                .createdAt(LocalDateTime.now())
                .build();

        Competition c3 = Competition.builder()
                .teamA(brazil).teamB(morocco)
                .stadium(akron)
                .date(LocalDate.of(2026, 5, 20))
                .time(LocalTime.of(13 + (r.nextInt(9)), 0))
                .createdAt(LocalDateTime.now())
                .scoreA(4)
                .scoreB(3)
                .build();

        Competition c4 = Competition.builder()
                .teamA(haiti).teamB(scotland)
                .stadium(lincolnFinancial)
                .date(LocalDate.of(2026, 5, 24))
                .time(LocalTime.of(13 + (r.nextInt(9)), 0))
                .createdAt(LocalDateTime.now())
                .build();

        Competition c5 = Competition.builder()
                .teamA(belgium).teamB(egypt)
                .stadium(mercedesBenz)
                .date(LocalDate.of(2026, 5, 30))
                .time(LocalTime.of(13 + (r.nextInt(9)), 0))
                .createdAt(LocalDateTime.now())
                .build();

        Competition c6 = Competition.builder()
                .teamA(iran).teamB(newZealand)
                .stadium(metLife)
                .date(LocalDate.of(2026, 6, 2))
                .time(LocalTime.of(13 + (r.nextInt(9)), 0))
                .createdAt(LocalDateTime.now())
                .build();

        Competition c7 = Competition.builder()
                .teamA(england).teamB(croatia)
                .stadium(gillette)
                .date(LocalDate.of(2026, 6, 6))
                .time(LocalTime.of(13 + (r.nextInt(9)), 0))
                .createdAt(LocalDateTime.now())
                .build();

        Competition c8 = Competition.builder()
                .teamA(ghana).teamB(panama)
                .stadium(hardRock)
                .date(LocalDate.of(2026, 6, 15))
                .time(LocalTime.of(13 + (r.nextInt(9)), 0))
                .createdAt(LocalDateTime.now())
                .build();

        competitionRepository.saveAll(List.of(c1,c2,c3,c4,c5,c6,c7,c8));

        // ── 11. PREDICTIONS  ──────────────────────────────────────────
        var allCompetitions = competitionRepository.findAll();
        var generatedPredictions = new ArrayList<Prediction>();
        for (User user : generatedUsers) {
            for (Competition competition : allCompetitions) {
                Prediction prediction = Prediction.builder()
                        .user(user)
                        .competition(competition)
                        .predictedScoreA(faker.number().numberBetween(0, 6))
                        .predictedScoreB(faker.number().numberBetween(0, 6))
                        .createdAt(LocalDateTime.now())
                        .build();
                generatedPredictions.add(prediction);
            }
        }

        predictionRepository.saveAll(generatedPredictions);

        // -- RANKINGS ----------------------------
        /*List<Country> allCountries = countryRepository.findAll();
        List<Ranking> rankings = allCountries.stream()
                .map(c -> Ranking.builder()
                        .country(c)
                        .position(0)
                        .wins(0).draws(0).losses(0)
                        .goalsScored(0).goalsAgainst(0)
                        .points(0)
                        .build())
                .toList();

        rankingRepository.saveAll(rankings);*/

    }
    private void calculateRankings(List<Competition> competitions, Map<Country, Ranking> rankingMap) {
        for (Competition competition : competitions) {
            if (competition.getScoreA() == null || competition.getScoreB() == null) continue;

            Country teamA = competition.getTeamA();
            Country teamB = competition.getTeamB();
            int scoreA = competition.getScoreA();
            int scoreB = competition.getScoreB();

            Ranking rankA = rankingMap.get(teamA);
            Ranking rankB = rankingMap.get(teamB);

            if (scoreA > scoreB) {
                rankA.setWins(rankA.getWins() + 1);
                rankA.setPoints(rankA.getPoints() + 3);
                rankB.setLosses(rankB.getLosses() + 1);
            } else if (scoreB > scoreA) {
                rankB.setWins(rankB.getWins() + 1);
                rankB.setPoints(rankB.getPoints() + 3);
                rankA.setLosses(rankA.getLosses() + 1);
            } else {
                rankA.setDraws(rankA.getDraws() + 1);
                rankA.setPoints(rankA.getPoints() + 1);
                rankB.setDraws(rankB.getDraws() + 1);
                rankB.setPoints(rankB.getPoints() + 1);
            }
        }
    }
}
