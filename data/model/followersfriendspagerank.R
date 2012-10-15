user <- read.table('User.dat', header=TRUE)
attach(user)
png("followersfriendspagerank.png", width=480, height=480)
plot(followers-friends, pageRank, col="grey", xlab="(Followers - Friends)", ylab="PageRank")
lines(lowess(followers-friends, pageRank))
dev.off()


