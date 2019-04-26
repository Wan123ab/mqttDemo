package com.wq.mqttdemo.constant;

public enum QosType {

    MOST_ONCE {
        public int getType() {
            return 0;
        }

        public String getDesc() {
            return "最多一次";
        }

    },
    LEAST_ONCE {
        public int getType() {
            return 1;
        }

        public String getDesc() {
            return "最少一次";
        }

    },
    ONLYONCE {
        public int getType() {
            return 2;
        }

        public String getDesc() {
            return "刚好一次";
        }

    };

}
