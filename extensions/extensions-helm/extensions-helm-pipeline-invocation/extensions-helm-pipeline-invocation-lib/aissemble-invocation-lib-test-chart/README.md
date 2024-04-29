# Helm Lib Test Module
Due to the manner in which library charts function, we cannot test them in-place.  Therefore,
we have created this helper module which treats the library as a subchart.  The resultant
helm charts are not published.