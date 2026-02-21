package com.weehong.checkstyle.checks;

// Mock annotations for testing
@interface Test {}
@interface Before {}
@interface After {}
@interface BeforeEach {}
@interface AfterEach {}
@interface BeforeAll {}
@interface AfterAll {}
@interface PostConstruct {}
@interface PreDestroy {}
@interface Bean {}
@interface Scheduled {}
@interface EventListener {}
@interface GetMapping {}
@interface PostMapping {}
@interface PreAuthorize {}
@interface Around {}

public class InputUnusedMethodCheckAnnotations {

    // Methods with framework annotations should NOT trigger violations
    @Test
    private void testMethod() {}

    @Before
    private void beforeMethod() {}

    @After
    private void afterMethod() {}

    @BeforeEach
    private void beforeEachMethod() {}

    @AfterEach
    private void afterEachMethod() {}

    @BeforeAll
    private static void beforeAllMethod() {}

    @AfterAll
    private static void afterAllMethod() {}

    @PostConstruct
    private void postConstructMethod() {}

    @PreDestroy
    private void preDestroyMethod() {}

    @Bean
    private Object beanMethod() { return null; }

    @Scheduled
    private void scheduledMethod() {}

    @EventListener
    private void eventListenerMethod() {}

    @GetMapping
    private void getMappingMethod() {}

    @PostMapping
    private void postMappingMethod() {}

    @PreAuthorize
    private void preAuthorizeMethod() {}

    @Around
    private void aroundMethod() {}
}
