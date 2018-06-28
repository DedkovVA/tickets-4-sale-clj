(ns t4s.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clj-time.core :as jtc]
    [clj-time.format :as jtf]
    )
  (:import (org.joda.time DateTime))
)

(def data-file (io/resource "back/shows.csv" ))

(defn read-file [f]
  (-> (slurp f)
      (str/split-lines)))

(def file-lines (map (fn [line] (str/replace line #"\r" "")) (read-file data-file)))

(def lines (map (fn [line] (str/split line #"(?<=\"),|,(?=\")")) file-lines))

(defn replace-quotes [s] (str/replace s #"\"" ""))

(defn normalize-str [s] (str/lower-case (replace-quotes s)))

(def custom-formatter (jtf/formatter "yyyy-MM-dd"))

(defn str-to-date [s] (jtf/parse custom-formatter s))

(defn diff-in-days
  [^DateTime dt1 ^DateTime dt2] (
                                  let [before (jtc/before? dt1 dt2)
                                       dts (if before [dt1 dt2] [dt2 dt1])
                                       diff (jtc/in-days (jtc/interval (dts 0) (dts 1)))]
                                  (if before diff (- diff))
                                  )
  )

(def raw-shows (map (fn [v] (
                       hash-map
                       :show (normalize-str (v 0))
                       :dt (str-to-date (v 1))
                       :genre (keyword (normalize-str (v 2)))
                     )
               ) lines))

(def genres {:musical 70 :comedy 50 :drama 40 :nonFound 0})

(defn discount [price] (* 0.8 price))

(def num-of-days-show-runs 100)
(def num-of-days-before-discount 80)
(def num-of-days-big-hall 60)
(def num-of-days-before-sale 25)
(def num-of-days-sold-out 5)

(defn to-tickets [left available price] {:left left :available available :price price})
(defn to-show [raw-show status-to-tickets]
  {:raw-show raw-show :status (status-to-tickets 0) :tickets (status-to-tickets 1)})

(defn eval-show-availability [query-dt show-dt opening-dt genre]
  (let [query-show-diff (diff-in-days query-dt show-dt)
        opening-show-diff (diff-in-days opening-dt show-dt)
        [total per-day] (if (< opening-show-diff num-of-days-big-hall) [200 10] [100 5])
        initial-price (genres genre)
        price (if (< opening-show-diff num-of-days-before-discount) initial-price (discount initial-price))
        null-tickets (to-tickets 0 0 0)]
    (cond
      (>= opening-show-diff num-of-days-show-runs) [:in-the-past null-tickets]
      (>= query-show-diff num-of-days-before-sale) [:sale-non-started (to-tickets total 0 price)]
      (and (>= query-show-diff num-of-days-sold-out) (< query-show-diff num-of-days-before-sale))
      (let [left (- total (* (- (- num-of-days-before-sale query-show-diff) 1) per-day))]
        [:in-the-past (to-tickets left per-day price)])
      :else [:sold-out null-tickets])
    ))

(defn filter-shows [show-dt]
  (filter (fn [raw-show] (>= (diff-in-days (raw-show :dt) show-dt) 0)) raw-shows))

(defn raw-shows-to-info [query-dt show-dt raw-shows]
  (map (fn [raw-show] (to-show
                        raw-show
                        (eval-show-availability query-dt show-dt (raw-show :dt) (raw-show :genre))
                        )) raw-shows))

(defn get-shows [query-dt show-dt] (raw-shows-to-info query-dt show-dt (filter-shows show-dt)))

(defn get-shows-json [query-dt show-dt]
  (map
    (fn [show]
      {
       :genre ((show :raw-show) :genre)
       :show ((show :raw-show) :show)
       :dt ((show :raw-show) :dt)
       :status (show :status)
       :left ((show :tickets) :left)
       :available ((show :tickets) :available)
       :price ((show :tickets) :price)
       }) (get-shows query-dt show-dt)))

(defn sort-by-dt [raw-shows] (sort-by :title (sort-by :dt jtc/after? raw-shows)))

(defn map-values [m f]
  (into {} (for [[k v] m] [k (f v)])))

;vector is transformed to list
(defn group-by-genre [query-dt show-dt]
    (map-values
      (group-by :genre (sort-by-dt (get-shows-json query-dt show-dt)))
      (fn [v] (map (fn [x] (dissoc x :genre :dt)) v)))
  )