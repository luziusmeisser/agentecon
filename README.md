# Seemingly Equivalent Heuristics

Run com.agentecon.verification.ExplorationScenario to get the raw data for chart
https://github.com/kronrod/agentecon/blob/StrategyExploration/heuristics.pdf

It compares three different heuristics for calculating dividend payments.

My recommendation is to use

d = (1-l) E[R]

with l being labor share, and E[R] today's expected revenues given price belief and goods for sale.

This is more stable than using the standard profit function d = R - C with the known values from yesterday.