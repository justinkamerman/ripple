#!/usr/bin/python -u

import sys
import tweepy
import time
import logging

CONSUMER_KEY = 'k2hL6ySpFuQmHalJvNfJA'
CONSUMER_SECRET = 'E2R7zNbFNsCNo7GzshImADFhjz8cDVhQSWDRG2ceH8'
ACCESS_KEY = '80157513-xrvAvUgPtodmMkUEYHWO1pDxztN1urQNHWcZUQDUE'
ACCESS_SECRET = 'TKwuT7ZXI6zl4ymp1ohFjwM7mes1eIKj5PESmHIQk'

auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth.set_access_token(ACCESS_KEY, ACCESS_SECRET)
api = tweepy.API(auth)
limit = api.rate_limit_status()
print (limit)


