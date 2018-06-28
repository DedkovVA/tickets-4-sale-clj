(defproject tickets-4-sale-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src-back" "src-front"]

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-time "0.14.4"]
                 [compojure "1.6.1"]
                 [org.clojure/data.json "0.2.6"]
                 [ring/ring-defaults "0.3.2"]

                 ;front
                 [org.clojure/clojurescript "1.10.339"]
                 [cljs-ajax "0.7.3"]
                 [reagent "0.8.1"]
                 [figwheel "0.5.16"]
                 ]
  :plugins [
            [lein-ring "0.12.4"]

            ;front
            [lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.16"]
            ]

  :ring {:handler t4s.routes/site}

  :figwheel {
             :repl false
             :http-server-root "front"
             :ring-handler t4s.routes/site
             }

  :profiles {:dev {:resource-paths ["target/cljsbuild/client"]}}

  :cljsbuild
  {:builds
   {:client
    {:source-paths ["src-front"]
     :figwheel true
     :compiler {:parallel-build true
                :source-map true
                :optimizations :none
                :main "t4s.page"
                :output-dir "target/cljsbuild/client/front/js/out"
                :output-to "target/cljsbuild/client/front/js/main.js"
                :asset-path "js/out"}}}}
)