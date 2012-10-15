user <- read.table('User.dat', header=TRUE)

png("statusespagerank.png", width=480, height=480)
plot(statuses, pageRank, col="grey", xlab="Tweets", ylab="PageRank")
lines(lowess(statuses, pageRank))
dev.off()


