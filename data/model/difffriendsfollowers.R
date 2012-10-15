user <- read.table('User.dat', header=TRUE)

png("difffriendsfollowers.png", width=480, height=480)
hist(friends-followers, xlim=c(0,2000), breaks=1000000, main="", xlab="(Friends - Followers)")
dev.off()


