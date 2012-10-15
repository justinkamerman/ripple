user <- read.table('User.dat', header=TRUE)

png("friendshist.png", width=480, height=480)
hist(friends, xlim=c(0,10000), breaks=10000, main="", xlab="Number of Friends")
dev.off()


