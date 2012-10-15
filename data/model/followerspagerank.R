user <- read.table('User.dat', header=TRUE)

png("followerspagerank.png", width=480, height=480)
plot(followers, pageRank, col="grey", xlab="Number of Followers", ylab="PageRank")
lines(lowess(followers, pageRank))
dev.off()


