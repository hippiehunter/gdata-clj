(ns gdata-clj.test.contacts
  (:require [gdata-clj.contacts :as con])
  (:require [gdata-clj.test.utils :as utils])
  (:use [clojure.test]))

(deftest basics
  (let [login-token (utils/do-oauth-login con/contacts-scope )
        contacts-service (con/contacts-service login-token)
        contacts (con/contacts contacts-service)]
      (is contacts "ohnoes")))