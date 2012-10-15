library("Hmisc")

png("pagerankiterations.png", width=480, height=480)

iter1 <- read.table('iter1/pagerank.dat', header=TRUE)
iter2 <- read.table('iter2/pagerank.dat', header=TRUE)
iter3 <- read.table('iter3/pagerank.dat', header=TRUE)
iter4 <- read.table('iter4/pagerank.dat', header=TRUE)
iter5 <- read.table('iter5/pagerank.dat', header=TRUE)
iter6 <- read.table('iter6/pagerank.dat', header=TRUE)

plot(density (iter1$pagerank, n=10000), xlim=c(0,0.005), ylim=c(0, 10000), xlab="PageRank", ylab="Density", main="")
lines(density (iter2$pagerank, n=10000), xlim=c(0,0.005), ylim=c(0, 10000))
lines(density (iter3$pagerank, n=10000), xlim=c(0,0.005), ylim=c(0, 10000))
lines(density (iter4$pagerank, n=10000), xlim=c(0,0.005), ylim=c(0, 10000))
lines(density (iter5$pagerank, n=10000), xlim=c(0,0.005), ylim=c(0, 10000))

dev.off()
