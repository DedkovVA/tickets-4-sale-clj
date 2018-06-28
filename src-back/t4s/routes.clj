(ns t4s.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [clj-time.core :as jtc]
            [t4s.core :as core]
            [ring.middleware.defaults :refer :all]
            [ring.util.response :as resp]
            )
  )

(defroutes app
           (GET "/query"
                [show-dt]
             {
              :status 200
              :headers {"Content-Type" "application/json; charset=utf-8"}
              :body (json/write-str
                      (core/group-by-genre
                        (jtc/today-at 0 0)
                        (core/str-to-date show-dt)))
              })
           (GET "/" [] (-> (resp/resource-response "index.html" {:root "front"})
                           (resp/content-type "text/html")))
           (route/not-found "<h1>Page not found</h1>"))

(def site (wrap-defaults app site-defaults))
