package com.muththamizh.lovely;

import java.util.List;

public interface QuoteResponseListener {
    void didFetch(List<QuoteResponse> response, String message);
    void didError(String message);
}
