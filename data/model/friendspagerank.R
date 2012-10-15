user <- read.table('User.dat', header=TRUE)

png("friendspagerank.png", width=480, height=480)
plot(friends, pageRank, col="grey", xlab="Number of Friends", ylab="PageRank")
lines(lowess(friends, pageRank))
dev.off()


