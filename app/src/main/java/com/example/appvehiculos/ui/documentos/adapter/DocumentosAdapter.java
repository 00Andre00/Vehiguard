package com.example.appvehiculos.ui.documentos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appvehiculos.R;

import java.util.List;

public class DocumentosAdapter extends RecyclerView.Adapter<DocumentosAdapter.DocumentViewHolder> {

    private List<Document> documents;
    private OnItemClickListener listener;

    public DocumentosAdapter(List<Document> documents) {
        this.documents = documents;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);
        holder.bind(document);
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Document document);
    }

    class DocumentViewHolder extends RecyclerView.ViewHolder {
        ImageView documentImageView;
        TextView documentNameTextView;

        DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            documentImageView = itemView.findViewById(R.id.documentImageView);
            documentNameTextView = itemView.findViewById(R.id.documentNameTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(documents.get(position));
                }
            });
        }

        void bind(Document document) {
            if (document.isImage()) {
                Glide.with(itemView.getContext())
                        .load(document.getFileUrl())
                        .into(documentImageView);
                documentNameTextView.setText("Imagen");
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.pdfwallpaper)
                        .into(documentImageView);
                documentNameTextView.setText("PDF");
            }
        }
    }
}