Twitter PageRank
================

A MapReduce calculation of PageRank applied to the Twitter
social-graph. Social-graph sampled via a crawler, using the
Twitter REST API. The crawler performs a breadth first traversal of
the Twitter graph starting at an arbitrary user node and storing
connections in a MySQL database schema.  A simplified PageRank
algorithm was applied to the collected social graph data to calculate
rank scores for all users. The simplified algorithm does not include
the damping factor meant to account for a jump by a web surfer to a
random web page. However, this omission probably accounts in part for
the fact that our implementation converges to a uniform PageRank
distribution of zero if allowed to run for too many iterations. A more
in-depth analysis would have to include a some analogous damping
factor in order to converge towards more meaningful values.

Included are R scripts to analyse results, SQL DDL scripts, and a 
MapReduce job to generate adjacency lists from raw cralwer data.

[![Followers Histogram](https://github.com/justinkamerman/Ripple/raw/master/images/followershist.png)](https://github.com/justinkamerman/Ripple/raw/master/images/followershist.png)

[![Friends Histogram](https://github.com/justinkamerman/Ripple/raw/master/images/friendshist.png)](https://github.com/justinkamerman/Ripple/raw/master/images/friendshist.png)

[![followersfriendspagerank](https://github.com/justinkamerman/Ripple/raw/master/images/followersfriendspagerank.png)](https://github.com/justinkamerman/Ripple/raw/master/images/followersfriendspagerank.png)

[![followerspagerank](https://github.com/justinkamerman/Ripple/raw/master/images/followerspagerank.png)](https://github.com/justinkamerman/Ripple/raw/master/images/followerspagerank.png)

[![friendspagerank](https://github.com/justinkamerman/Ripple/raw/master/images/friendspagerank.png)](https://github.com/justinkamerman/Ripple/raw/master/images/friendspagerank.png)

[![statusespagerank](https://github.com/justinkamerman/Ripple/raw/master/images/statusespagerank.png)](https://github.com/justinkamerman/Ripple/raw/master/images/statusespagerank.png)

[![difffriendsfollowers](https://github.com/justinkamerman/Ripple/raw/master/images/difffriendsfollowers.png)](https://github.com/justinkamerman/Ripple/raw/master/images/difffriendsfollowers.png)

[![pagerankiterations](https://github.com/justinkamerman/Ripple/raw/master/images/pagerankiterations.png)](https://github.com/justinkamerman/Ripple/raw/master/images/pagerankiterations.png)

[![rankadjustment](https://github.com/justinkamerman/Ripple/raw/master/images/rankadjustment.png)](https://github.com/justinkamerman/Ripple/raw/master/images/rankadjustment.png)
