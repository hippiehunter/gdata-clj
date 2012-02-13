(ns gdata-clj.contacts
  (:import [com.google.api.client.util
            Key])
  (:import [java.util
            List])
  (:import [com.google.api.client.http
            HttpTransport])
  (:import [com.google.api.client.http.javanet
            NetHttpTransport])
  (:import [com.google.api.client.json.jackson
            JacksonFactory])
  (:import [com.google.api.client.googleapis
            GoogleUrl])
  (:import [com.google.gdata.data.contacts
            ContactFeed
            ContactEntry])
  (:import [com.google.gdata.client
            Query])
  (:import [com.google.gdata.client.contacts
            ContactsService])
  (:use [gdata-clj.oauth]))

(def *max-feed-length* 50)                                         

(def contacts-scope
  "https://www.google.com/m8/feeds/contacts")

(def contacts-feed-url 
  "https://www.google.com/m8/feeds/contacts/default/full")

(defn contacts [^ContactsService service]
    (defn paginate-feed [service pos]
      (let [paginated-query (doto (Query. (java.net.URL. contacts-feed-url))
                              (.setStartIndex pos))
            ^ContactFeed feed (.query service paginated-query ContactFeed)
            feed-seq (seq (.getEntries feed))
            feed-length (count feed-seq)]
        (if (= feed-length *max-feed-length*)
          (lazy-cat feed-seq (paginate-feed service (+ pos feed-length)))
          feed-seq)))
    (paginate-feed service 1))

(defn contacts-service 
  ([auth-token] 
    (let [net-transport (NetHttpTransport.)
          json-factory (JacksonFactory.)
          {:keys [access-token refresh-token]} (get-auth net-transport json-factory auth-token)]
      (contacts-service access-token refresh-token)))
    
  ([access-token refresh-token]
    (doto (ContactsService. *application-name*)
      (.setHeader "Authorization" (str "Bearer " access-token)))))

(defprotocol IContact
  (name [contact])
  (nickname [contact])
  (phone-numbers [contact])
  (postal-addresses [contact])
  (websites [contact])
  (email-addresses [contact])
  (languages [contact])
  (gender [contact])
  (birthday [contact]))

(extend-type ContactEntry
  IContact
  (name [contact] 
        (if (.hasName contact)
          (.getName contact)
          nil))
  (nickname [contact] 
            (if (.hasNickname contact)
              (.getNickname contact)
              nil))
  (phone-numbers [contact]
                 (if (.hasPhoneNumbers contact)
                   (seq (.getPhoneNumbers contact))
                   '()))
  (postal-addresses [contact]
                    (if (.hasPostalAddresses contact)
                      (seq (.getPostalAddresses contact))
                      '()))
  (websites [contact]
            (if (.hasWebsites contact)
              (seq (.getWebsites contact))
              '()))
  (email-addresses [contact]
                   (if (.hasEmailAddresses contact)
                     (seq (.getEmailAddresses contact))
                     '()))
  (languages [contact]
             (if (.hasLanguages contact)
               (seq (.getLanguages contact))
               '()))
  
  (gender [contact]
          (if (.hasGender contact)
            (.getGender contact)
            nil))
  
  (birthday [contact]
            (if (.hasBirthday contact)
              (.getBirthday contact)
              nil)))
  

