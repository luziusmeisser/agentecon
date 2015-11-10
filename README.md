# Seemingly Equivalent Heuristics (#ExplorationChart)

This configuration is part of the master's thesis [Mastering Agent-Based Economics](http://master.agentecon.com/thesis.pdf).

Its results can be browsed online on http://master.agentecon.com/sim.html?id=ExplorationChart (not usable on mobile, tested on Chrome and Edge under Windows 10, might not work in other setups)

Run [com.agentecon.verification.ExplorationScenario](https://github.com/kronrod/agentecon/blob/StrategyExploration/src/com/agentecon/verification/ExplorationScenario.java) to get the raw data for chart [heuristics.pdf](https://github.com/kronrod/agentecon/blob/StrategyExploration/heuristics.pdf).

It compares three different heuristics for calculating dividend payments.

My recommendation is to use

d = (1-l) E[R]

with l being labor share, and E[R] today's expected revenues given price belief and goods for sale.