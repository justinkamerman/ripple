#!/usr/bin/python -u

import sys
import tweepy
import time
import logging
from optparse import OptionParser
import MySQLdb

CONSUMER_KEY = 'k2hL6ySpFuQmHalJvNfJA'
CONSUMER_SECRET = 'E2R7zNbFNsCNo7GzshImADFhjz8cDVhQSWDRG2ceH8'
ACCESS_KEY = '80157513-xrvAvUgPtodmMkUEYHWO1pDxztN1urQNHWcZUQDUE'
ACCESS_SECRET = 'TKwuT7ZXI6zl4ymp1ohFjwM7mes1eIKj5PESmHIQk'


#----------------------------------------
def objdump (obj):
#----------------------------------------
    for attr in dir(obj):
        print "obj.%s = %s" % (attr, getattr(obj, attr))

#----------------------------------------
def configureLogging (logger):
#----------------------------------------
    logger = logging.getLogger('ripple-crawler')
    logger.setLevel (logging.DEBUG)
    fh = logging.FileHandler('ripple-crawler.log')
    fh.setLevel(logging.DEBUG)
    ch = logging.StreamHandler()
    ch.setLevel(logging.DEBUG)
    # create formatter and add it to the handlers
    logging.Formatter.converter = time.localtime
    formatter = logging.Formatter('[%(asctime)s] [%(levelname)s] %(message)s')
    fh.setFormatter(formatter)
    ch.setFormatter(formatter)
    # add the handlers to the logger
    logger.addHandler(fh)
    logger.addHandler(ch)

#----------------------------------------
def userToString (user):
#----------------------------------------
    string = "[[id=%d][name=%s][screen_name=%s][followers_count=%d][friends_count=%d][statuses_count=%d]]" % (user.id, user.name, user.screen_name, user.followers_count, user.friends_count, user.statuses_count)
    return string


#----------------------------------------
def processFollower (user, follower):
#----------------------------------------
    rc = 1
    try:
        con = MySQLdb.Connection ("localhost","justin","zinkwazi","ripple", charset = "utf8", use_unicode = True) 
        cursor = con.cursor ()
        cursor.execute("""INSERT INTO User (id, name, screen_name, followers_count, friends_count, statuses_count) VALUES (%(id)s, %(name)s, %(screen_name)s, %(followers_count)s, %(friends_count)s, '%(statuses_count)s')""", follower.__dict__)
        con.commit ()
        con.close ()
        rc = 0
    except MySQLdb.Error, e:
        if ( e.args[0] == 1062 ):
            LOG.info ("Ignoring duplicate user: %s" % (userToString(follower)))
        else:
            LOG.error ("Caught MySQLdb.Error %d: %s" % (e.args[0], e.args[1]))
    finally:
        if con.open:
            con.close ()

    return rc


#----------------------------------------
# Main
#----------------------------------------
LOG = logging.getLogger('ripple-crawler')
configureLogging (LOG)


# Command line options
user = ""
parser = OptionParser()
parser.add_option ("-u", "--user", dest="initial_user", help="Id or screen name of Twitter user from which to start the traversal")
(options, args) = parser.parse_args ()
if options.initial_user is None:
    parser.error ("Initial user is required")
    sys.exit (1)
else:
    user = options.initial_user


# Twitter authentication
auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth.set_access_token(ACCESS_KEY, ACCESS_SECRET)
api = tweepy.API(auth)
LOG.info ("Authenticated with Twitter API")
LOG.debug ("Starting graph traversal from user %s" % options.initial_user);


# Main loop
count = 1
follower_cursors = tweepy.Cursor (api.followers, id = user)
followers_iter = follower_cursors.items()
follower = None
while True:
    try:
        # We may have to retry a failed
        if ( follower is None ):
            follower = followers_iter.next()
        LOG.debug ("Adding %s follower #%d %s" % (user, count, follower.screen_name))
        rc = processFollower (user, follower)
        if ( rc == 0 ):
            follower = None
            count += 1

    except StopIteration:
        break
    except tweepy.error.TweepError as (err):
        LOG.error ("Caught TweepError: %s" % (err))
        limit = api.rate_limit_status()
        if (limit['remaining_hits'] == 0):
            seconds_until_reset = int (limit['reset_time_in_seconds'] - time.time())
            LOG.info ("API request limit reached. Sleeping for %s seconds" % seconds_until_reset)
            time.sleep (seconds_until_reset + 5)
        else:
            LOG.info ("Sleeping a few seconds and then retrying")
            time.sleep (5)

                
                
                
                
                
