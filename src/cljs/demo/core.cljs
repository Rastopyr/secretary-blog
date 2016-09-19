(ns demo.core
  (:require-macros [om.core :as om]
                   [secretary.core :refer [defroute]])
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [secretary.core :as sec
             :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(sec/set-config! :prefix "#")

(declare app-state
         outlet)


(defn outlet [data owner]
  (let [{:keys [current-route route-components]} (om/get-shared owner)
        index (or (om/get-state owner :outlet-index) 0)
        [route _] (om/observe owner (current-route))
        components (get route-components route)]
    (if (> (count components) index)
      (let [component (nth components index)]
        (om/build component data {:state {:outlet-index (inc index)}}))
      nil)))

(defn root [data owner]
  (om/component (outlet data owner)))

(defroute home-path "/" []
  (swap! app-state assoc :route [:home {}]))

(defroute github-path "/github" []
  (swap! app-state assoc :route [:github {}]))

(defn home [data owner]
  (om/component
   (dom/a #js {:href (github-path)} "View posts")))

(defn github [data owner]
  (om/component
    (dom/a #js {:href "https://github.com/rastopyr"} "Github")))

(defn main [data owner]
  (om/component
   (dom/div nil
            (dom/h1 nil
                    (dom/a #js {:href (home-path)} "Nested Routing Example"))
            (outlet data owner))))

(def route-components
  {
    :home [main home]
    :github [main github]
  })


(defn current-route []
  (om/ref-cursor (:route (om/root-cursor app-state))))

(enable-console-print!)

(def app-state (atom {:route [:home {}]
                      :posts [{:id 1
                               :title "Lorem ipsum"
                               :details "Lorem ipsum dolor sit amet, consectetur adipiscing elit."}
                              {:id 2
                               :title "Morbi porta"
                               :details "Morbi porta tortor mauris, sit amet tincidunt libero malesuada nec."}]}))

(sec/set-config! :prefix "#")

(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(sec/dispatch! (.-token %)))
  (doto h (.setEnabled true)))

(om/root root
         app-state
         {:target (. js/document (getElementById "app"))
          :shared {:current-route current-route
                   :route-components route-components}})
