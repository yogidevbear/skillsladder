(ns clj.new.edge-app-template
  (:require [clj.new.templates :refer [renderer project-name name-to-path ->files
                                       multi-segment sanitize-ns
                                       *force?* *dir*]]))

(def render (renderer "app.template"))

(defn- generate-port
  "Generates 'pretty' port sequences in the form of:
  xx00
  xyxy"
  []
  (let [base 20
        upper-limit 99
        dd-port (+ (rand-int (- (inc upper-limit) base)) base)]
    (rand-nth
      [(format "%d%d" dd-port dd-port)
       (format "%d00" dd-port)])))

(defn edge-app-template
  "FIXME: write documentation"
  [name & opts]
  (binding [*dir* (sanitize-ns name)]
    (let [opts (set (map #(keyword (subs % 2)) opts))
          data {:name (project-name name)
                :sanitized (name-to-path name)
                :root-ns (multi-segment (sanitize-ns name))
                :sass (contains? opts :sass)
                :cljs (contains? opts :cljs)
                :kick (or (contains? opts :sass)
                          (contains? opts :cljs))
                :server-port (generate-port)
                :figwheel-port (generate-port)}]
      (println (str "Generating fresh 'clj new' edge.app-template project into " *dir* "."))
      (->files data
               ["deps.edn" (render "deps.edn" data)]
               ["src/{{sanitized}}/foo.clj" (render "foo.clj" data)]
               ["src/config.edn" (render "config.edn" data)]
               ["dev/dev.clj" (render "dev.clj" data)]
               ["dev/log_dev_app.properties" (render "log_dev_app.properties" data)]
               [".dir-locals.el" (render "dir-locals.el" data)])
      (binding [*force?* true]
        (when (:kick data)
          (->files data
                   ["src/index.html" (render "index.html" data)]
                   ["target/dev/.gitkeep" ""]
                   ["target/prod/.gitkeep" ""]))
        (if (:sass data)
          (->files data
                   ["src/{{name}}.scss" (render "app.css" data)])
          (->files data
                   ["src/public/{{name}}.css" (render "app.css" data)]))
        (when (:cljs data)
          (->files data
                   ["src/{{sanitized}}/frontend/main.cljs" (render "main.cljs" data)]))))))
