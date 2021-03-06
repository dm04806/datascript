(ns datascript.test.query-find-specs
  (:require-macros
    [cemerick.cljs.test :refer [is are deftest testing]])
  (:require
    [datascript.core :as dc]
    [datascript :as d]
    [cemerick.cljs.test :as t]
    [datascript.test.core :as tdc]))

(deftest test-find-specs
  (let [db (-> (d/empty-db)
               (d/db-with [ [:db/add 1 :name "Petr"]
                            [:db/add 1 :age 44]
                            [:db/add 2 :name "Ivan"]
                            [:db/add 2 :age 25]
                            [:db/add 3 :name "Sergey"]
                            [:db/add 3 :age 11] ]))]
    (is (= (set (d/q '[:find [?name ...]
                        :where [_ :name ?name]] db))
           #{"Ivan" "Petr" "Sergey"}))
    (is (= (d/q '[:find [?name ?age]
                  :where [1 :name ?name]
                         [1 :age  ?age]] db)
           ["Petr" 44]))
    (is (= (d/q '[:find ?name .
                  :where [1 :name ?name]] db)
           "Petr"))
    
    (testing "Multiple results get cut"
      (is (contains?
            #{["Petr" 44] ["Ivan" 25] ["Sergey" 11]}
            (d/q '[:find [?name ?age]
                   :where [?e :name ?name]
                          [?e :age  ?age]] db)))
      (is (contains?
            #{"Ivan" "Petr" "Sergey"}
            (d/q '[:find ?name .
                   :where [_ :name ?name]] db))))
    
    (testing "Aggregates work with find specs"
      (is (= (d/q '[:find [(count ?name) ...]
                    :where [_ :name ?name]] db)
             [3]))
      (is (= (d/q '[:find [(count ?name)]
                    :where [_ :name ?name]] db)
             [3]))
      (is (= (d/q '[:find (count ?name) .
                    :where [_ :name ?name]] db)
             3)))
    ))
