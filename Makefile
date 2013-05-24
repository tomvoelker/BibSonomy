MODULES=bibsonomy-common bibsonomy-model bibsonomy-rest-common bibsonomy-rest-client bibsonomy-bibtex-parser bibsonomy-scraper bibsonomy-scrapingservice bibsonomy-layout bibsonomy-rest-client-oauth bibsonomy-web-common

define deploy-target
  deploy:: ; cd $1 && mvn deploy
endef

# go over all modules and call mvn deploy
$(foreach module,$(MODULES),$(eval $(call deploy-target,$(module))))
