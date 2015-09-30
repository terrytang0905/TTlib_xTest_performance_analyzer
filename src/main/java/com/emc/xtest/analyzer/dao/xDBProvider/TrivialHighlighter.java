package com.emc.xtest.analyzer.dao.xDBProvider;

import java.util.Arrays;
import java.util.Iterator;

import com.xhive.query.interfaces.XhiveXQueryExtensionFunctionIf;
import com.xhive.query.interfaces.XhiveXQueryValueIf;

/**
 * A trivial "highlighter" that just returns the full text search tokens to allow testing whether
 * the right tokens are returned.
 */
public class TrivialHighlighter implements XhiveXQueryExtensionFunctionIf {

  @Override
  public Object[] call(Iterator<? extends XhiveXQueryValueIf>[] args) {
    int length = args.length;
    // no tokens
    if (length <= 1) {
      return args;
    }
    // no highlight parameters
    if (length == 3) {
      return Arrays.copyOfRange(args, 0, 1);
    }

    return new Object[] { args[0], args[args.length - 1] };
  }


}
