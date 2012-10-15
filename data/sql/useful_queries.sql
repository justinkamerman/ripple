# Compare users followers count to the number of followers represented in the database
select screen_name, followee_id, count(follower_id), followers_count, statuses_count from Follower, User where User.id = followee_id  group by followee_id order by count(follower_id) desc limit 100;

# Compare users friends count to the number of friends represented in the database
select screen_name, follower_id, count(followee_id), friends_count, statuses_count from Follower, User where User.id = follower_id  group by follower_id order by count(followee_id) desc;

# Number of unique followers
select count(distinct(follower_id)) from Follower;

# Number of unique friends
select count(distinct(followee_id)) from Follower;

# Create Follower extract (initial AdjList input)
select follower_id, followee_id from Follower into outfile '/tmp/Follower.dat';

# Create User extract
select id, followers_count, friends_count, statuses_count from User into outfile '/tmp/User.dat';

# Load Hadoop output file into MySQL
load data local infile '/home/justin/wa/unb/unb/cs6905/ripple/data/set/ipr1/iter3/pagerank.dat' into table PageRank fields terminated by ' ' lines terminated by '\n' (id, pageRank);