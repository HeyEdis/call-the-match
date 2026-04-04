package com.example.callthematch.config;

import com.example.callthematch.model.*;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.CountryRepository;
import com.example.callthematch.repository.GoalRepository;
import com.example.callthematch.repository.GroupMemberRepository;
import com.example.callthematch.repository.GroupRepository;
import com.example.callthematch.repository.LocationRepository;
import com.example.callthematch.repository.PredictionRepository;
import com.example.callthematch.repository.RankingRepository;
import com.example.callthematch.repository.StadiumRepository;
import com.example.callthematch.repository.TeamMemberRepository;
import com.example.callthematch.repository.TeamRepository;
import com.example.callthematch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("dev") // laten weten enkel in development
public class InitDataConfig implements CommandLineRunner {

    private final CompetitionRepository competitionRepository;
    private final CountryRepository countryRepository;
    private final GoalRepository goalRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final LocationRepository locationRepository;
    private final PredictionRepository predictionRepository;
    private final RankingRepository rankingRepository;
    private final StadiumRepository stadiumRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        User admin = User.builder()
                .email("admin@example.com")
                .passwordHash("password")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User user1 = User.builder()
                .email("alice@example.com")
                .passwordHash("password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .email("bob@example.com")
                .passwordHash("password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.saveAll(List.of(admin,user1,user2));

        // ── 2. COUNTRIES ──────────────────────────────────────────────────────
        Country usa          = Country.builder().landCode(1).name("United States").build();
        Country mexico       = Country.builder().landCode(52).name("Mexico").build();
        Country canada       = Country.builder().landCode(1).name("Canada").build();
        Country japan        = Country.builder().landCode(81).name("Japan").build();
        Country iran         = Country.builder().landCode(98).name("Iran").build();
        Country southKorea   = Country.builder().landCode(82).name("South Korea").build();
        Country australia    = Country.builder().landCode(61).name("Australia").build();
        Country saudiArabia  = Country.builder().landCode(966).name("Saudi Arabia").build();
        Country qatar        = Country.builder().landCode(974).name("Qatar").build();
        Country uzbekistan   = Country.builder().landCode(998).name("Uzbekistan").build();
        Country jordan       = Country.builder().landCode(962).name("Jordan").build();
        Country iraq         = Country.builder().landCode(964).name("Iraq").build();
        Country argentina    = Country.builder().landCode(54).name("Argentina").build();
        Country brazil       = Country.builder().landCode(55).name("Brazil").build();
        Country uruguay      = Country.builder().landCode(598).name("Uruguay").build();
        Country colombia     = Country.builder().landCode(57).name("Colombia").build();
        Country ecuador      = Country.builder().landCode(593).name("Ecuador").build();
        Country paraguay     = Country.builder().landCode(595).name("Paraguay").build();
        Country newZealand   = Country.builder().landCode(64).name("New Zealand").build();
        Country morocco      = Country.builder().landCode(212).name("Morocco").build();
        Country senegal      = Country.builder().landCode(221).name("Senegal").build();
        Country egypt        = Country.builder().landCode(20).name("Egypt").build();
        Country algeria      = Country.builder().landCode(213).name("Algeria").build();
        Country tunisia      = Country.builder().landCode(216).name("Tunisia").build();
        Country southAfrica  = Country.builder().landCode(27).name("South Africa").build();
        Country ivoryCoast   = Country.builder().landCode(225).name("Ivory Coast").build();
        Country ghana        = Country.builder().landCode(233).name("Ghana").build();
        Country capeVerde    = Country.builder().landCode(238).name("Cape Verde").build();
        Country drCongo      = Country.builder().landCode(243).name("DR Congo").build();
        Country england      = Country.builder().landCode(44).name("England").build();
        Country france       = Country.builder().landCode(33).name("France").build();
        Country spain        = Country.builder().landCode(34).name("Spain").build();
        Country germany      = Country.builder().landCode(49).name("Germany").build();
        Country portugal     = Country.builder().landCode(351).name("Portugal").build();
        Country netherlands  = Country.builder().landCode(31).name("Netherlands").build();
        Country belgium      = Country.builder().landCode(32).name("Belgium").build();
        Country croatia      = Country.builder().landCode(385).name("Croatia").build();
        Country switzerland  = Country.builder().landCode(41).name("Switzerland").build();
        Country austria      = Country.builder().landCode(43).name("Austria").build();
        Country scotland     = Country.builder().landCode(44).name("Scotland").build();
        Country norway       = Country.builder().landCode(47).name("Norway").build();
        Country bosnia       = Country.builder().landCode(387).name("Bosnia and Herzegovina").build();
        Country sweden       = Country.builder().landCode(46).name("Sweden").build();
        Country turkey       = Country.builder().landCode(90).name("Turkey").build();
        Country czechia      = Country.builder().landCode(420).name("Czechia").build();
        Country panama       = Country.builder().landCode(507).name("Panama").build();
        Country curacao      = Country.builder().landCode(599).name("Curacao").build();
        Country haiti        = Country.builder().landCode(509).name("Haiti").build();

        countryRepository.saveAll(List.of(
                usa, mexico, canada, japan, iran, southKorea, australia, saudiArabia,
                qatar, uzbekistan, jordan, iraq, argentina, brazil, uruguay, colombia,
                ecuador, paraguay, newZealand, morocco, senegal, egypt, algeria, tunisia,
                southAfrica, ivoryCoast, ghana, capeVerde, drCongo, england, france,
                spain, germany, portugal, netherlands, belgium, croatia, switzerland,
                austria, scotland, norway, bosnia, sweden, turkey, czechia, panama,
                curacao, haiti
        ));

        // ── 3. GROUPS ─────────────────────────────────────────────────────────
        CountryGroup groupA = CountryGroup.builder().label("A").build();
        CountryGroup groupB = CountryGroup.builder().label("B").build();
        CountryGroup groupC = CountryGroup.builder().label("C").build();
        CountryGroup groupD = CountryGroup.builder().label("D").build();
        CountryGroup groupE = CountryGroup.builder().label("E").build();
        CountryGroup groupF = CountryGroup.builder().label("F").build();
        CountryGroup groupG = CountryGroup.builder().label("G").build();
        CountryGroup groupH = CountryGroup.builder().label("H").build();
        CountryGroup groupI = CountryGroup.builder().label("I").build();
        CountryGroup groupJ = CountryGroup.builder().label("J").build();
        CountryGroup groupK = CountryGroup.builder().label("K").build();
        CountryGroup groupL = CountryGroup.builder().label("L").build();

        groupRepository.saveAll(List.of(
                groupA, groupB, groupC, groupD, groupE, groupF,
                groupG, groupH, groupI, groupJ, groupK, groupL
        ));

        // ── 4. GROUP MEMBERS ──────────────────────────────────────────────────
        // Group A: Mexico, South Korea, South Africa, Czechia
        // Group B: Canada, Switzerland, Qatar, Bosnia
        // Group C: Brazil, Scotland, Morocco, Haiti
        // Group D: USA, Australia, Paraguay, Turkey
        // Group E: Germany, Ecuador, Ivory Coast, Curacao
        // Group F: Netherlands, Japan, Tunisia, Sweden
        // Group G: Belgium, Iran, Egypt, New Zealand
        // Group H: France, Norway, Senegal, Iraq
        // Group I: Argentina, Austria, Algeria, Jordan
        // Group J: Spain, Uruguay, Saudi Arabia, Cape Verde
        // Group K: Portugal, Colombia, Uzbekistan, DR Congo
        // Group L: England, Croatia, Ghana, Panama

        List<GroupMember> groupMembers = List.of(
                GroupMember.builder().group(groupA).country(mexico).build(),
                GroupMember.builder().group(groupA).country(southKorea).build(),
                GroupMember.builder().group(groupA).country(southAfrica).build(),
                GroupMember.builder().group(groupA).country(czechia).build(),

                GroupMember.builder().group(groupB).country(canada).build(),
                GroupMember.builder().group(groupB).country(switzerland).build(),
                GroupMember.builder().group(groupB).country(qatar).build(),
                GroupMember.builder().group(groupB).country(bosnia).build(),

                GroupMember.builder().group(groupC).country(brazil).build(),
                GroupMember.builder().group(groupC).country(scotland).build(),
                GroupMember.builder().group(groupC).country(morocco).build(),
                GroupMember.builder().group(groupC).country(haiti).build(),

                GroupMember.builder().group(groupD).country(usa).build(),
                GroupMember.builder().group(groupD).country(australia).build(),
                GroupMember.builder().group(groupD).country(paraguay).build(),
                GroupMember.builder().group(groupD).country(turkey).build(),

                GroupMember.builder().group(groupE).country(germany).build(),
                GroupMember.builder().group(groupE).country(ecuador).build(),
                GroupMember.builder().group(groupE).country(ivoryCoast).build(),
                GroupMember.builder().group(groupE).country(curacao).build(),

                GroupMember.builder().group(groupF).country(netherlands).build(),
                GroupMember.builder().group(groupF).country(japan).build(),
                GroupMember.builder().group(groupF).country(tunisia).build(),
                GroupMember.builder().group(groupF).country(sweden).build(),

                GroupMember.builder().group(groupG).country(belgium).build(),
                GroupMember.builder().group(groupG).country(iran).build(),
                GroupMember.builder().group(groupG).country(egypt).build(),
                GroupMember.builder().group(groupG).country(newZealand).build(),

                GroupMember.builder().group(groupH).country(france).build(),
                GroupMember.builder().group(groupH).country(norway).build(),
                GroupMember.builder().group(groupH).country(senegal).build(),
                GroupMember.builder().group(groupH).country(iraq).build(),

                GroupMember.builder().group(groupI).country(argentina).build(),
                GroupMember.builder().group(groupI).country(austria).build(),
                GroupMember.builder().group(groupI).country(algeria).build(),
                GroupMember.builder().group(groupI).country(jordan).build(),

                GroupMember.builder().group(groupJ).country(spain).build(),
                GroupMember.builder().group(groupJ).country(uruguay).build(),
                GroupMember.builder().group(groupJ).country(saudiArabia).build(),
                GroupMember.builder().group(groupJ).country(capeVerde).build(),

                GroupMember.builder().group(groupK).country(portugal).build(),
                GroupMember.builder().group(groupK).country(colombia).build(),
                GroupMember.builder().group(groupK).country(uzbekistan).build(),
                GroupMember.builder().group(groupK).country(drCongo).build(),

                GroupMember.builder().group(groupL).country(england).build(),
                GroupMember.builder().group(groupL).country(croatia).build(),
                GroupMember.builder().group(groupL).country(ghana).build(),
                GroupMember.builder().group(groupL).country(panama).build()

        );

        groupMemberRepository.saveAll(groupMembers);

        // ── 5. LOCATIONS ──────────────────────────────────────────────────────
        Location newYork    = Location.builder().city("New York / New Jersey").build();
        Location dallas     = Location.builder().city("Dallas").build();
        Location atlanta    = Location.builder().city("Atlanta").build();
        Location losAngeles = Location.builder().city("Los Angeles").build();
        Location kansasCity = Location.builder().city("Kansas City").build();
        Location houston    = Location.builder().city("Houston").build();
        Location sanFran    = Location.builder().city("San Francisco").build();
        Location seattle    = Location.builder().city("Seattle").build();
        Location philadelphia = Location.builder().city("Philadelphia").build();
        Location miami      = Location.builder().city("Miami").build();
        Location boston     = Location.builder().city("Boston").build();
        Location mexicoCity = Location.builder().city("Mexico City").build();
        Location monterrey  = Location.builder().city("Monterrey").build();
        Location guadalajara = Location.builder().city("Guadalajara").build();
        Location toronto    = Location.builder().city("Toronto").build();
        Location vancouver  = Location.builder().city("Vancouver").build();

        locationRepository.saveAll(List.of(
                newYork, losAngeles, dallas, sanFran, miami, seattle,
                boston, houston, atlanta, kansasCity, mexicoCity,
                guadalajara, monterrey, toronto, vancouver, philadelphia
        ));

        // ── 6. STADIUMS ───────────────────────────────────────────────────────
        Stadium metLife     = Stadium.builder().location(newYork).name("MetLife Stadium").code(1001).capacity(82500).build();
        Stadium attStadium  = Stadium.builder().location(dallas).name("AT&T Stadium").code(1003).capacity(80000).build();
        Stadium mercedesBenz = Stadium.builder().location(atlanta).name("Mercedes-Benz Stadium").code(1009).capacity(71000).build();
        Stadium sofi        = Stadium.builder().location(losAngeles).name("SoFi Stadium").code(1002).capacity(70240).build();
        Stadium levisStad   = Stadium.builder().location(sanFran).name("Levi's Stadium").code(1004).capacity(68500).build();
        Stadium hardRock    = Stadium.builder().location(miami).name("Hard Rock Stadium").code(1005).capacity(65326).build();
        Stadium lumen       = Stadium.builder().location(seattle).name("Lumen Field").code(1006).capacity(69000).build();
        Stadium gillette    = Stadium.builder().location(boston).name("Gillette Stadium").code(1007).capacity(65878).build();
        Stadium nrg         = Stadium.builder().location(houston).name("NRG Stadium").code(1008).capacity(72220).build();
        Stadium arrowhead   = Stadium.builder().location(kansasCity).name("Arrowhead Stadium").code(1010).capacity(76416).build();
        Stadium azteca      = Stadium.builder().location(mexicoCity).name("Estadio Azteca").code(1011).capacity(87523).build();
        Stadium akron       = Stadium.builder().location(guadalajara).name("Estadio Akron").code(1012).capacity(49850).build();
        Stadium bbva        = Stadium.builder().location(monterrey).name("Estadio BBVA").code(1013).capacity(53500).build();
        Stadium bmo         = Stadium.builder().location(toronto).name("BMO Field").code(1014).capacity(45736).build();
        Stadium bcPlace     = Stadium.builder().location(vancouver).name("BC Place").code(1015).capacity(54500).build();
        Stadium lincolnFinancial = Stadium.builder().location(philadelphia).name("Lincoln Financial Field").code(1016).capacity(69000).build();

        stadiumRepository.saveAll(List.of(
                metLife, sofi, attStadium, levisStad, hardRock, lumen,
                gillette, nrg, mercedesBenz, arrowhead, azteca, akron,
                bbva, bmo, bcPlace, lincolnFinancial
        ));

        // ── 7. TEAMS (friend groups) ───────────────────────────────────────────
        Team team1 = Team.builder()
                .name("The Predictors")
                .inviteCode("PRED2026")
                .owner(user1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Team team2 = Team.builder()
                .name("Goal Hunters")
                .inviteCode("GOAL2026")
                .owner(user2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        teamRepository.saveAll(List.of(team1, team2));

        // ── 8. TEAM MEMBERS ───────────────────────────────────────────────────
        TeamMember member1 = TeamMember.builder()
                .user(user1).team(team1).score(0)
                .joinedAt(LocalDateTime.now()).build();

        TeamMember member2 = TeamMember.builder()
                .user(user2).team(team2).score(0)
                .joinedAt(LocalDateTime.now()).build();

        teamMemberRepository.saveAll(List.of(member1, member2));

        // ── 9. RANKINGS ───────────────────────────────────────────────────────
        List<Country> allCountries = countryRepository.findAll();
        List<Ranking> rankings = allCountries.stream()
                .map(c -> Ranking.builder()
                        .country(c)
                        .position(0)
                        .wins(0).draws(0).losses(0)
                        .goalsScored(0).goalsAgainst(0)
                        .points(0)
                        .build())
                .toList();

        rankingRepository.saveAll(rankings);

        // ── 10. COMPETITIONS (sample group stage matches) ─────────────────────
        // A few sample matches per group to demonstrate the structure
        // Group A: Mexico, South Korea, South Africa, Czechia
        Competition compA1 = Competition.builder()
                .teamA(mexico).teamB(southKorea)
                .stadium(azteca).group(groupA)
                .stage(Stage.GROUP_STAGE)
                .date(LocalDate.of(2026, 6, 12))
                .time(LocalTime.of(18, 0))
                .createdAt(LocalDateTime.now())
                .build();

        Competition compA2 = Competition.builder()
                .teamA(southAfrica).teamB(czechia)
                .stadium(attStadium).group(groupA)
                .stage(Stage.GROUP_STAGE)
                .date(LocalDate.of(2026, 6, 12))
                .time(LocalTime.of(21, 0))
                .createdAt(LocalDateTime.now())
                .build();

        // Group B: Canada, Switzerland, Qatar, Bosnia
        Competition compB1 = Competition.builder()
                .teamA(canada).teamB(switzerland)
                .stadium(bmo).group(groupB)
                .stage(Stage.GROUP_STAGE)
                .date(LocalDate.of(2026, 6, 13))
                .time(LocalTime.of(18, 0))
                .createdAt(LocalDateTime.now())
                .build();

        // Group C: Brazil, Scotland, Morocco, Haiti
        Competition compC1 = Competition.builder()
                .teamA(brazil).teamB(scotland)
                .stadium(metLife).group(groupC)
                .stage(Stage.GROUP_STAGE)
                .date(LocalDate.of(2026, 6, 14))
                .time(LocalTime.of(18, 0))
                .createdAt(LocalDateTime.now())
                .build();

        // Group D: USA, Australia, Paraguay, Turkey
        Competition compD1 = Competition.builder()
                .teamA(usa).teamB(australia)
                .stadium(sofi).group(groupD)
                .stage(Stage.GROUP_STAGE)
                .date(LocalDate.of(2026, 6, 14))
                .time(LocalTime.of(21, 0))
                .createdAt(LocalDateTime.now())
                .build();

        competitionRepository.saveAll(List.of(compA1, compA2, compB1, compC1, compD1));

        // ── 11. PREDICTIONS (sample) ──────────────────────────────────────────
        Prediction pred1 = Prediction.builder()
                .user(user1).competition(compA1)
                .predictedScoreA(2).predictedScoreB(1)
                .build();

        Prediction pred2 = Prediction.builder()
                .user(user2).competition(compA1)
                .predictedScoreA(1).predictedScoreB(1)
                .build();

        predictionRepository.saveAll(List.of(pred1, pred2));

        // ── 12. GOAL EVENTS (sample) ──────────────────────────────────────────
        Goal goal1 = Goal.builder()
                .competition(compA1)
                .minute(23)
                .isTeamA(true)
                .playerName("Hirving Lozano")
                .scoreA(1).scoreB(0)
                .build();

        Goal goal2 = Goal.builder()
                .competition(compA1)
                .minute(67)
                .isTeamA(false)
                .playerName("Son Heung-min")
                .scoreA(1).scoreB(1)
                .build();

        goalRepository.saveAll(List.of(goal1, goal2));

    }
}
