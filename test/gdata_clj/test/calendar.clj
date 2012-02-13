(ns gdata-clj.test.calendar
  (:require [gdata-clj.calendar :as cal])
  (:require [gdata-clj.test.utils :as utils])
  (:use [clojure.test]))

(deftest basics
  (let [login-token (utils/do-oauth-login cal/calendar-scope )
        calendar-service (cal/calendar-service login-token)
        calendars (cal/calendars calendar-service)]
      (is calendars "ohnoes")))