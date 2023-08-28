# [3.0.0](https://github.com/gravitee-io/gravitee-common/compare/2.2.0...3.0.0) (2023-08-28)


### chore

* **deps:** update gravitee-parent ([db20ebc](https://github.com/gravitee-io/gravitee-common/commit/db20ebc99432f84444198b3565d747d5ed4fc738))


### Features

* add utility method to get list from an object ([22f267e](https://github.com/gravitee-io/gravitee-common/commit/22f267e44b0996f03d89bd81fb4e65265b556656))


### BREAKING CHANGES

* **deps:** require Java17

# [2.2.0](https://github.com/gravitee-io/gravitee-common/compare/2.1.1...2.2.0) (2023-08-18)


### Features

* initialize a keystore from PEM certifcates (files and content) ([8352886](https://github.com/gravitee-io/gravitee-common/commit/83528869f4ceaa622a190152d1d6a59430b58d8e))

## [2.1.1](https://github.com/gravitee-io/gravitee-common/compare/2.1.0...2.1.1) (2023-05-17)


### Bug Fixes

* do not allow trailing hyphen in ID ([5fd81dd](https://github.com/gravitee-io/gravitee-common/commit/5fd81ddea6d388cdee711e4ac05a8ceaaead8349))

# [2.1.0](https://github.com/gravitee-io/gravitee-common/compare/2.0.0...2.1.0) (2023-03-17)


### Bug Fixes

* create private method to deal with empty string ([2281473](https://github.com/gravitee-io/gravitee-common/commit/2281473688a38d2b931d02dbd0da403817d73c56))
* **deps:** upgrade gravitee-bom ([5f84a78](https://github.com/gravitee-io/gravitee-common/commit/5f84a784fbc6e76fb17a194aceda21e8951f3b45))
* remove the use of Spring framework ([d107040](https://github.com/gravitee-io/gravitee-common/commit/d10704046343cebd0e878d4521b7dd229efe16ff))


### Features

* add retry on predicate capabilities ([50e68a8](https://github.com/gravitee-io/gravitee-common/commit/50e68a83169c972d966eded3b85f6d4f82d33973))
* add uri isAbsolute util function ([2c30a06](https://github.com/gravitee-io/gravitee-common/commit/2c30a0691859ac5680f68aedddcac25b70163ac8))
* implement duration parser to manage simple and iso8601 format ([7b562cb](https://github.com/gravitee-io/gravitee-common/commit/7b562cb8d580fe0f70bfbbbb46c6896527928560))

# [2.1.0-alpha.5](https://github.com/gravitee-io/gravitee-common/compare/2.1.0-alpha.4...2.1.0-alpha.5) (2023-01-23)


### Bug Fixes

* create private method to deal with empty string ([c8dbfa7](https://github.com/gravitee-io/gravitee-common/commit/c8dbfa7201db5d6df04757129c637b5834624b0f))

# [2.1.0-alpha.4](https://github.com/gravitee-io/gravitee-common/compare/2.1.0-alpha.3...2.1.0-alpha.4) (2023-01-20)


### Bug Fixes

* remove the use of Spring framework ([cbc5fc8](https://github.com/gravitee-io/gravitee-common/commit/cbc5fc8283303d0b28f9c540200a37aa24e79f92))

# [2.1.0-alpha.3](https://github.com/gravitee-io/gravitee-common/compare/2.1.0-alpha.2...2.1.0-alpha.3) (2023-01-16)


### Features

* implement duration parser to manage simple and iso8601 format ([d26a6ae](https://github.com/gravitee-io/gravitee-common/commit/d26a6aea55c5110775c3e700beedba8ccf7ab039))

# [2.1.0-alpha.2](https://github.com/gravitee-io/gravitee-common/compare/2.1.0-alpha.1...2.1.0-alpha.2) (2023-01-12)


### Features

* add retry on predicate capabilities ([606d7c9](https://github.com/gravitee-io/gravitee-common/commit/606d7c94fb71b5058c633482b7b04e297f54a742))

# [2.1.0-alpha.1](https://github.com/gravitee-io/gravitee-common/compare/2.0.0...2.1.0-alpha.1) (2023-01-11)


### Features

* add uri isAbsolute util function ([33768d8](https://github.com/gravitee-io/gravitee-common/commit/33768d80bfd6955ec62d60ac986a0d9402492c7f))

# [2.0.0](https://github.com/gravitee-io/gravitee-common/compare/1.28.0...2.0.0) (2022-12-09)


### Bug Fixes

* missing backpressure on retry ([664f659](https://github.com/gravitee-io/gravitee-common/commit/664f659d1b984bb609f4a283e4786c9a45f7846e))
* use passwordToCharArray to properly handle password ([84867ac](https://github.com/gravitee-io/gravitee-common/commit/84867ac1b3717f0a460c3328d9335c5df72e36f3))


### chore

* bump to rxJava3 ([90b65d2](https://github.com/gravitee-io/gravitee-common/commit/90b65d2730765cf57e3a1945fd3853f48bc4e4c4))


### Features

* add rx helpers method to retry a flowable with delay between attempts ([a30cad0](https://github.com/gravitee-io/gravitee-common/commit/a30cad0662b9aad20cd729189b5c5e9674650f71))


### BREAKING CHANGES

* rxJava3 required

# [2.0.0-alpha.4](https://github.com/gravitee-io/gravitee-common/compare/2.0.0-alpha.3...2.0.0-alpha.4) (2022-12-06)


### Bug Fixes

* missing backpressure on retry ([664f659](https://github.com/gravitee-io/gravitee-common/commit/664f659d1b984bb609f4a283e4786c9a45f7846e))

# [2.0.0-alpha.3](https://github.com/gravitee-io/gravitee-common/compare/2.0.0-alpha.2...2.0.0-alpha.3) (2022-11-25)


### Features

* add rx helpers method to retry a flowable with delay between attempts ([a30cad0](https://github.com/gravitee-io/gravitee-common/commit/a30cad0662b9aad20cd729189b5c5e9674650f71))

# [2.0.0-alpha.2](https://github.com/gravitee-io/gravitee-common/compare/2.0.0-alpha.1...2.0.0-alpha.2) (2022-11-15)


### Bug Fixes

* use passwordToCharArray to properly handle password ([84867ac](https://github.com/gravitee-io/gravitee-common/commit/84867ac1b3717f0a460c3328d9335c5df72e36f3))

# [2.0.0-alpha.1](https://github.com/gravitee-io/gravitee-common/compare/1.28.0...2.0.0-alpha.1) (2022-10-18)


### chore

* bump to rxJava3 ([90b65d2](https://github.com/gravitee-io/gravitee-common/commit/90b65d2730765cf57e3a1945fd3853f48bc4e4c4))


### BREAKING CHANGES

* rxJava3 required

# [1.28.0](https://github.com/gravitee-io/gravitee-common/compare/1.27.0...1.28.0) (2022-10-04)


### Features

* add RxHelper.mergeWithFirst method ([84a70ce](https://github.com/gravitee-io/gravitee-common/commit/84a70ceba01be3b6bf621e8636d541d8c3c4bfde))

# [1.27.0](https://github.com/gravitee-io/gravitee-common/compare/1.26.1...1.27.0) (2022-06-10)


### Features

* **jupiter:** mutualize LazyJWT for reusability ([5f89228](https://github.com/gravitee-io/gravitee-common/commit/5f89228af480f1ef565475ae6f5e33f0f4fb681b))

## [1.26.1](https://github.com/gravitee-io/gravitee-common/compare/1.26.0...1.26.1) (2022-05-19)


### Bug Fixes

* init vertx proxy options from node configuration ([c8715b1](https://github.com/gravitee-io/gravitee-common/commit/c8715b12ca2376c2161c1dea86f465033dea1faa))

# [1.26.0](https://github.com/gravitee-io/gravitee-common/compare/1.25.0...1.26.0) (2022-05-12)


### Features

* add vertx proxy options util ([7ebf64d](https://github.com/gravitee-io/gravitee-common/commit/7ebf64d31ae36843e60a8b8383135de4c427cafd))
