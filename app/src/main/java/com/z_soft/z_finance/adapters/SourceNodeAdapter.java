package com.z_soft.z_finance.adapters;

import android.content.Intent;

import android.support.v4.app.ActivityOptionsCompat;
import android.view.ViewGroup;

import com.z_soft.z_finance.activities.EditSourceActivity;
import com.z_soft.z_finance.adapters.abstracts.TreeNodeListAdapter;
import com.z_soft.z_finance.adapters.holders.SourceViewHolder;
import com.z_soft.z_finance.core.database.Initializer;
import com.z_soft.z_finance.core.impls.DefaultSource;
import com.z_soft.z_finance.core.interfaces.Source;
import com.z_soft.z_finance.utils.AppContext;

import java.util.List;

public class SourceNodeAdapter extends TreeNodeListAdapter<Source, SourceViewHolder> {

    private static final String TAG = SourceNodeAdapter.class.getName();

    public SourceNodeAdapter(int mode) {
        super(mode, Initializer.getSourceManager(), Initializer.getSourceManager().getAll());
    }

    public SourceNodeAdapter(int mode, List<Source> initialList) {
        super(mode, Initializer.getSourceManager(), initialList);

    }


    @Override
    public SourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        super.onCreateViewHolder(parent, viewType);// обязательно нужно вызывать
        return new SourceViewHolder(itemView);
    }

    @Override
    protected void openActivityOnClick(Source source, int requestCode) {

        Source s = null;

        switch (requestCode){
            case AppContext.REQUEST_NODE_ADD_CHILD: // для редактирования создаем новый пустой объект
                s = new DefaultSource();
                s.setOperationType(source.getOperationType());
                break;

            default:s=source;
        }

        // если будет передан любой другой requestCode - тогда просто передавать объект в intent как есть

        Intent intent = new Intent(activityContext, EditSourceActivity.class); // какой акивити хотим вызвать
        intent.putExtra(AppContext.NODE_OBJECT, s); // помещаем выбранный объект node для передачи в активити
        activityContext.startActivityForResult(intent, requestCode, ActivityOptionsCompat.makeSceneTransitionAnimation(activityContext).toBundle()); // устанавливаем анимацию перехода

    }

}
