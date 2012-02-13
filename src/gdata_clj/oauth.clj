(ns gdata-clj.oauth
  (:import [com.google.api.client.googleapis.auth.oauth2.draft10
           GoogleAuthorizationRequestUrl]))

(def ^:dynamic *application-name* "test_product")
(def ^:dynamic *client-id* "yourid")
(def ^:dynamic *client-secret* "yoursecret")
(def ^:dynamic *redirect-url* "urn:ietf:wg:oauth:2.0:oob")

(defn authorization-url [scope redirect-url]
  (.build (GoogleAuthorizationRequestUrl. *client-id* redirect-url scope)))

(defn get-auth [net-transport json-factory auth-code]
  (let [auth-grant-response (.execute 
                              (com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest$GoogleAuthorizationCodeGrant. 
                                net-transport 
                                json-factory
                                *client-id*
                                *client-secret*
                                auth-code
                                *redirect-url*))]
    {:access-token (.accessToken auth-grant-response)
     :refresh-token (.refreshToken auth-grant-response)
     :expiration (.expiresIn auth-grant-response)
     :scope (.scope auth-grant-response)}))


