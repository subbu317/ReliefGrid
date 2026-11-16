# Executed Java allocation-engine evaluation

The Java 21 `AllocationEngine.java` was compiled with `javac` and run with a standalone `Runner` that exercised the **actual project engine** using a seeded 5-shelter, 20-request, 10-volunteer, 4-category synthetic scenario. Spring Boot integration tests and the React build were **not** run in the generation environment because Maven was unavailable and dependency registries were inaccessible.

| Metric | FCFS | Min-cost flow |
|---|---:|---:|
| Units allocated | 109 | 109 |
| High-priority demand fulfilled | 60.47% | 100.00% |
| Unmet demand | 34 | 34 |
| Standard deviation of shelter fulfillment ratios (lower better) | 0.0745 | 0.1920 |
| Distance × units (km·units; lower better) | 358.35 | 484.47 |
| Cap violations verified by assertions | 0 | 0 |
| Computation time in one execution | 28.434 ms | 70.395 ms |

These measurements are from one executed Java run in the provided environment, not statistically rigorous benchmarks. The priority-focused optimizer improves high-urgency fulfilment while performing **worse** in fairness dispersion and distance in this instance. It is a multi-objective tradeoff, not a universal improvement. Run `mvn test` on your PC to validate Spring Boot integration and refresh performance measurements. The standalone runner used for this check is included in `evaluation/StandaloneBenchmark.java`; it compiles with only the category enum and allocation engine.
