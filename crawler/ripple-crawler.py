#!/usr/bin/python -u

import sys
import tweepy
import time
import logging
from optparse import OptionParser
import MySQLdb
import Queue


WORK_QUEUE = 'ripple-crawler'
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
def initLogging (logger):
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
def processFriend (user, friend):
#----------------------------------------
    con = MySQLdb.Connection ("localhost","justin","zinkwazi","ripple", charset = "utf8", use_unicode = True) 
    cursor = con.cursor ()

    try:        
        cursor.execute("""INSERT INTO User (id, name, screen_name, followers_count, friends_count, statuses_count) VALUES (%(id)s, %(name)s, %(screen_name)s, %(followers_count)s, %(friends_count)s, '%(statuses_count)s')""", 
                       friend.__dict__)
       
    except MySQLdb.Error, e:
        if ( e.args[0] == 1062 ):
            LOG.info ("Ignoring duplicate user: %s" % (friend.screen_name))
        else:
            LOG.error ("Caught MySQLdb.Error %d: %s" % (e.args[0], e.args[1]))
            if con.open:
                con.commit ()
                con.close ()
            raise

    try:
        if ( user is not None ):
            cursor.execute("""INSERT INTO Follower (followee_id, follower_id) VALUES (%s, %s)""", 
                           (friend.id, user.id))
    except MySQLdb.Error, e:
        if ( e.args[0] == 1062 ):
            LOG.info ("Ignoring duplicate %s friend %s" % (user.screen_name, friend.screen_name))
        else:
            LOG.error ("Caught MySQLdb.Error %d: %s" % (e.args[0], e.args[1]))
            raise
    finally:
        if con.open:
            con.commit ()
            con.close ()


#----------------------------------------
def initWorkQueue ():
#----------------------------------------
    return Queue.Queue()


#----------------------------------------
def enqueueWork (queue, item):
#----------------------------------------
    queue.put (item)
    

#----------------------------------------
def dequeueWork (queue):
#----------------------------------------
    return queue.get ()
    

#----------------------------------------
# Main
#----------------------------------------
LOG = logging.getLogger('ripple-crawler')
initLogging (LOG)
QUEUE = initWorkQueue ()


# Command line options
user = ""
parser = OptionParser()
parser.add_option ("-u", "--user", dest="initial_user", help="Id or screen name of Twitter user from which to start the traversal")
(options, args) = parser.parse_args ()
if options.initial_user is None:
    parser.error ("Initial user is required")
    sys.exit (1)

# Twitter authentication
auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth.set_access_token(ACCESS_KEY, ACCESS_SECRET)
api = tweepy.API(auth)
LOG.info ("Authenticated with Twitter API")
LOG.info ("Starting graph traversal from user %s" % options.initial_user);
enqueueWork (QUEUE, options.initial_user)


# BEGIN Main Loop
while True:
    # Fetch next user from queue
    user_id = dequeueWork (QUEUE)
    LOG.info ("Dequeued user id %s" % (user_id))
    while True:
        try:
            user = api.get_user (user_id)
            break
        except tweepy.error.TweepError as (err):
            LOG.error ("Caught TweepError getting user: %s" % (err))
            LOG.info ("Sleeping a few seconds and then retrying")
            time.sleep (5)

    LOG.info ("Processing user %s" % (userToString(user)))
    processFriend (None, user)
    
    # Friend loop
    count = 1
    friend_cursors = tweepy.Cursor (api.friends, id = user.id)
    friends_iter = friend_cursors.items()
    friend = None
    while True:
        try:
            # We may have to retry a failed friend lookup
            if ( friend is None ):
                friend = friends_iter.next()
                LOG.debug ("Adding %s friend #%d %s" % (user.screen_name, count, friend.screen_name))
                processFriend (user, friend)
                enqueueWork (QUEUE, friend.screen_name)
                friend = None
                count += 1
                
        except StopIteration:
            break
        except MySQLdb.Error, e:
            LOG.error ("Caught MySQLdb.Error %d: %s" % (e.args[0], e.args[1]))
        except tweepy.error.TweepError as (err):
            LOG.error ("Caught TweepError: %s" % (err))
            if (err.reason == "Not authorized" ):
                LOG.info ("Not authorized to see users followers. Skipping.")
                break
            limit = api.rate_limit_status()
            if (limit['remaining_hits'] == 0):
                seconds_until_reset = int (limit['reset_time_in_seconds'] - time.time())
                LOG.info ("API request limit reached. Sleeping for %s seconds" % seconds_until_reset)
                time.sleep (seconds_until_reset + 5)
            else:
                LOG.info ("Sleeping a few seconds and then retrying")
                time.sleep (5)

# END Main Loop
                
                
                
                
                
