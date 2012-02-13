(ns gdata-clj.calendar
  (:import [com.google.api.client.googleapis.auth.oauth2.draft10
           GoogleAccessProtectedResource]) 
  (:import [com.google.api.client.http
            HttpTransport])
  (:import [com.google.api.client.http.javanet
            NetHttpTransport])
  (:import [com.google.api.client.json.jackson
            JacksonFactory])
  (:import [com.google.api.client.util
            DateTime])
  (:import [com.google.api.services.calendar
            Calendar])
  (:import [com.google.api.client.auth.oauth2.draft10
            AccessTokenResponse])
  (:use [gdata-clj.oauth])
  (:import [com.google.api.services.calendar.model
            Event
            EventAttendee
            EventDateTime]))

(def calendar-scope "https://www.googleapis.com/auth/calendar")

(defn calendar-service 
  ([auth-code]
    (let [net-transport (NetHttpTransport.)
          json-factory (JacksonFactory.)
          {:keys [access-token refresh-token]} (get-auth net-transport json-factory auth-code)]
      (calendar-service access-token refresh-token)))
  
  ([access-token refresh-token]
    (let [net-transport (NetHttpTransport.)
          json-factory (JacksonFactory.)
          access-protected-resource (GoogleAccessProtectedResource.
                                      access-token
                                      net-transport
                                      json-factory
                                      *client-id*
                                      *client-secret*
                                      refresh-token)]
      (.build 
        (doto (Calendar/builder net-transport json-factory)
          (.setApplicationName *application-name*)
          (.setHttpRequestInitializer access-protected-resource))))))
    
  
(defn calendars [service] 
    (defn calendar-pagination [current-list]
      (if-let [page-token (.getNextPageToken current-list)]
        (lazy-cat (seq (.getItems current-list)) 
                  (calendar-pagination  
                    (->
                      (.calendarList service)
                      (.list)
                      (.setPageToken page-token)
                      (.execute))))
        (seq (.getItems current-list))))
    (calendar-pagination (->
                        (.calendarList service)
                        (.list)
                        (.execute))))

(defn calendar 
  ([service] (calendar service "primary"))
  ([service calendar-id] 
    (->
      (.calendars service)
      (.get calendar-id)
      (.execute))))

(defn events 
  ([service] (events service "primary"))
  ([service calendar-id] 
    (defn event-pagination [current-list]
      (if-let [page-token (.getNextPageToken current-list)]
        (lazy-cat (seq (.getItems current-list)) 
                  (event-pagination  
                    (->
                      (.events service)
                      (.list calendar-id)
                      (.setPageToken page-token)
                      (.execute))))))
    (event-pagination (->
                        (.events service)
                        (.list calendar-id)
                        (.execute)))))

(defn event
  ([service event-id] (event service "primary" event-id))
  ([service calendar-id event-id]
    (-> 
      (.events service)
      (.get calendar-id event-id)
      (.execute))))

(defn -make-event-attendee [attendee]
  (let [{:keys [email]} attendee]
    (doto (EventAttendee.)
      (.setEmail email))))

(defn create-event! [service & 
                     {:keys [calendar-id
                             summary
                             location
                             attendees
                             start-date
                             end-date
                             notification?]
                      :or {calendar-id "primary"
                           notification? false}}]
  (let [target-event (doto 
                       (Event.)
                       (.setSummary summary)
                       (.setLocation location)
                       (.setStart 
                         (doto 
                           (EventDateTime.)
                           (.setDateTime start-date)))
                       (.setEnd
                         (doto
                           (EventDateTime.)
                           (.setDateTime end-date)))
                       (.setAttendees
                         (java.util.ArrayList. (map -make-event-attendee attendees))))]
    (when notification?
      (.setSendNotifications true))
    
    (-> 
      (.events service)
      (.insert calendar-id target-event)
      (.execute))))
          
                         
                             
                                       
                                       
                                       
                                       
                                       
                                       