# Project Notes

Hexagonal architecture is an interesting way of modeling projects but does not justify small projects. I see its benefits on bigger projects
to enforce _some level of_ consistency on the models employed which is especially important if the project moves fast.

## On Hexagonal Architecture

Hexagonal architecture is an interesting way of modeling projects but does not justify small projects. I see its benefits on bigger projects
to enforce _some level of_ consistency on the models employed and promote decoupled components which are important qualities especially when the project moves fast.

## On leveraging DB for business rules

Moreover, I leveraged PostgreSQL constraints as means to ensure that the business rules were being met (e.g., uniqueness
and avoid shift overlap). The Hexagonal architecture practices suggest that these rules should be implemented on the domain
level instead. I kinda disagree as it would make the project far more complex, and I prefer having these rules as close as possible
from the source of truth. Being "pure" would require more I/O requests to the database transactional querying the database before
making the final update.

## Reactive

This project explored WebFlux and Reactive Databases. Some quirks I found:
- JSON deserialization is lazy leading to the lack of input validation on the controller level. For this reason, I reverted from Spring Data Reactive to plain Spring Data.
- The laziness on the stream can become a nuisance when testing as we need to materialize the stream to make stuff happen.
- Webflux Error Handling is different, not bad not good. Just different.

Last and not least, I recommend using Webflux by default on projects with intensive I/O operations (especially HTTP requests),
otherwise the benefits are minimal. However, as with every benchmarking question... just benchmark it!

## CI/CD

I very much enjoy leveraging `docker-compose` to setup reproducible testing environment:
- Jenkin machines do not need to have the dependencies installed.
- Ensures that the tests are reproducible regardless of the machines they run on. 
