(ns t4s.page
  (:require
    [ajax.core :refer [GET]]
    [ajax.core :refer [json-response-format]]
    [reagent.core :as r]
    ))

(def state (r/atom {:query-result nil :show-dt nil}))

(defn query []
  (GET (str "/query?show-dt=" (@state :show-dt))
        {
         :handler (fn [result]
                    (do
                      (swap! state assoc :query-result (js->clj result :keywordize-keys true))
                      )
                    )
         :response-format (json-response-format {:keywords? true :raw true})
         }))

(defn home []
  [:div {:class "m10"}
   [:div {:class "flex-container"}
    [:p "Show date:"]
    [:input
     {
      :type "text"
      :class "m10"
      :on-change (fn [e]
                   (swap! state assoc :show-dt (.. e -target -value))
                   )
      }]
    [:button {:type "submit"
              :class "pure-button pure-button-primary m10"
              :on-click query}
     "Submit"]
    ]
   (map (fn [e]
          (let [k (key e)]
            ^{:key k}
            [:div
             [:h6 k]
             [:table {:class "pure-table"}
              [:thead
               [:tr
                [:th "Title"]
                [:th "Tickets Left"]
                [:th "Tickets available"]
                [:th "Status"]
                [:th "Price"]
                ]]
              [:tbody
               (map
                 (fn [row]
                   ^{:key row}
                   [:tr
                    [:td (row :show)]
                    [:td (row :left)]
                    [:td (row :available)]
                    [:td (row :status)]
                    [:td (row :price)]
                    ]
                   ) (val e))]
              ]
             ]
            )
          ) (@state :query-result))
   ]
  )

(defn run [] (r/render [home] (js/document.getElementById "app")))

(run)