package com.karlgrauers.favorecipe.view_components.dialogbox;

import android.content.DialogInterface;

/*
 * Interface för klicklyssnare på dialogrutors
 * ok och cancel-knappar.
 */


public interface OnDialogClickListener {

    //Ok-klick, implementeras i klass som använder interfacet.
    void onDialogOkClicked(DialogInterface dialogInterface);

    //Cancel-klick, implementeras i klass som använder interfacet.
    void onDialogCancelClicked(DialogInterface dialogInterface);
}
