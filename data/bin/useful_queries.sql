# Compare users followers count to the number of followers represented in the database
select screen_name, followee_id, count(follower_id), followers_count, statuses_count from Follower, User where User.id = followee_id  group by followee_id order by count(follower_id) desc limit 100;

