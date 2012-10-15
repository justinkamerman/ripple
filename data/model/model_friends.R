library("MASS")

users = read.table ('users.dat')
colnames (users) = c('id', 'followers', 'friends', 'status')

# Friends
#par(mfrow=c(1,2))
friends.h <- hist (users$friends, breaks=100000, plot=FALSE)
friends.xhist <- c(friends.h$breaks)
friends.yhist <- c(friends.h$density, 0)
plot (friends.xhist, friends.yhist, type="s", xlim=c(0, 5000), ylim=c(0, 0.005))
lines (density(users$friends, n=10000, bw=200), col="red")

# Fit model to density function, not raw data

