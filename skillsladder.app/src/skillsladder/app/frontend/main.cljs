(ns ^:figwheel-hooks skillsladder.app.frontend.main)

(js/console.log "Hello, world")

;; This is called once
(defonce init
  (do (set! (.-innerHTML (js/document.getElementById "app"))
            "<p>Loaded app!</p>
            <p>Edit src/skillsladder/app/frontend/main.cljs to change this message.</p>")
      true))

;; This is called every time you make a code change
(defn ^:after-load reload []
  (set! (.-innerText (js/document.getElementById "app")) "Reloaded app!"))
