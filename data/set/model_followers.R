library("MASS")

users = read.table ('users.dat')
colnames (users) = c('id', 'followers', 'friends', 'status')

# Followers
par(mfrow=c(1,2))
followers.h <- hist (users$followers, breaks=1000000, plot=FALSE)
followers.xhist <- c(followers.h$breaks)
followers.yhist <- c(followers.h$density, 0)
followers.est <- fitdistr (users$followers+0.1, densfun="weibull")
followers.xfit <- seq (min(users$followers), max(users$followers), by=1)
followers.yfit <- with (as.list(coef(followers.est)), dweibull(followers.xfit, shape=shape, scale=scale))
plot (followers.xhist, followers.yhist, type="s", xlim=c(0, 1000))
lines (followers.xfit, followers.yfit, col="red")
qqplot (followers.yfit, followers.yhist, main="", ylab="", xlab="")
abline (0,1)
