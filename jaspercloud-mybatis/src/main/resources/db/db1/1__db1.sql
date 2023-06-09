-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS "public"."test";
CREATE TABLE "public"."test"
(
    "id"  serial4,
    "key" int4         NOT NULL,
    "val" varchar(255) NOT NULL
)
;
COMMENT ON COLUMN "public"."test"."id" IS '主键';
COMMENT ON COLUMN "public"."test"."key" IS '键';
COMMENT ON COLUMN "public"."test"."val" IS '内容';
COMMENT ON TABLE "public"."test" IS '测试表';
