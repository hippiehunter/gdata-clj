(ns gdata-clj.test.utils
  (:require [gdata-clj.oauth :as auth])
  (:require [clj-htmlunit
             [web-client :as wc]
             [html-page :as hp]
             [html-element :as he]]))

(defn do-oauth-login [scope]
  ;(binding [auth/*application-name* (do
  ;                               (println "type in your application name")
  ;                               (read-line))
  ;          auth/*client-id* (do
  ;                        (println "type in your app client-id")
  ;                        (read-line))
  ;          auth/*client-secret* (do
  ;                            (println "type in your app client-secret")
  ;                            (read-line))
  ;          
  ;          auth/*redirect-url* (do
  ;                           (println "type in your app's redirect-url")
  ;                           (read-line))]
          ;email (do
          ;        (println "type in google account email")
          ;        (read-line))
          ;password (do 
          ;           (println "type in google account password")
          ;           (read-line))
  (let [oauth-url (auth/authorization-url 
                      scope
                      "urn:ietf:wg:oauth:2.0:oob")
          client (wc/make-web-client 
                   :redirect? true
                   :java-script? false)
          auth-page (hp/get-page client oauth-url)
          email-element (doto (hp/element-by-id auth-page "Email")
                          (he/type! "your_account@google"))
          password-element (doto (hp/element-by-id auth-page "Passwd")
                             (he/type! "somepassword"))
          approval-page (-> 
                          (hp/element-by-id auth-page "signIn")
                          (he/click))
          approved-page (-> 
                          (hp/element-by-id approval-page "submit_approve_access")
                          (he/click))
          text-value (->
                       (hp/element-by-id approved-page "code")
                       (.getText))]
    text-value))