(ns demo.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [demo.core-test]
   [demo.common-test]))

(enable-console-print!)

(doo-tests 'demo.core-test
           'demo.common-test)
