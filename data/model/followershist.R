user <- read.table('User.dat', header=TRUE)

png("followershist.png", width=480, height=480)
hist(followers, xlim=c(0,10000), breaks=100000, main="", xlab="Number of Followers")
dev.off()
