# Reflection

## What I expected
I expected the guardrails to produce a safer implementation with fewer concurrency issues.

## What actually happened
The guarded version was longer and more complex. It added synchronization and handled observers registered after completion.

## Most important difference
The "Avoid race conditions" guardrail had the biggest impact, adding a lock, a completion flag, and custom observer registration.

## What I learned
Guardrails can improve correctness, but generated code still needs careful review and testing.

## What I changed
I expanded my four guardrails to seven with AI assistance. In the final code, I added `notified` to prevent duplicate notifications and `notifySafely` to handle observer exceptions.

## What I would do next time
I would test more edge cases, especially concurrent observer registration and callbacks that throw exceptions.

## Exercise-specific insight
The unconstrained version looked correct but could miss observers registered after completion. The race-condition guardrail helped prevent this problem, showing why synchronization is important beyond basic thread safety.