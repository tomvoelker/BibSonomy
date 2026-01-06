The v2 webapp module implements a very basic version of the bibsonomy homepage actually wired up to the (also new) v2 backend api, which also only has very rudamentary minimal functionality implemented up until this point.
Please:
- investigate the legacy bibsonomy webapp homepage implementation, both regarding functionality as well as design and document this in @bibsonomy-webapp-v2/docs/pages/HomePage.md (keep going, adding/updating the document as needed)
- use your chrome tooling to look at the current version of the page using a local dev deployment. for the backend you can either also deploy it locally yourself or use the currently deployed main branch at http://lsx-kubemaster-1.informatik.uni-wuerzburg.de:30627/). There
  1. fix all visual bugs and unevenness, making the homepage feel incredibly nice, polished and modern, whilst keeping the general design ideas from the legacy webapp homepage
  2. implement all features from the current homepage that are already possible using the existing apis
  3. @bibsonomy-rest-api-v2/next_apis.md document what needs to be implemented next to allow for all features of the current home page (not subsequent pages) to work.
- commit regularly!