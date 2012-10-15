library("Hmisc")

png("rankadjustment.png", width=480, height=480)
rankadj <- read.table('RankAdjustment.dat', header=TRUE)
plot (rankadj$iteration, rankadj$rankadj1/1000, xlab="Iteration", ylab="Total PageRank Adjustment")
lines (bezier(rankadj$iteration, rankadj$rankadj1/(1000)))
dev.off()
