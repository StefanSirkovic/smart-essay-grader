import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import type { EssayResponse } from '../types';
import StatusBadge from '../components/StatusBadge';
import {
  ArrowLeft,
  Sparkles,
  Trash2,
  Loader2,
  AlertCircle,
  RefreshCw,
} from 'lucide-react';

export default function EssayDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [essay, setEssay] = useState<EssayResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [grading, setGrading] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState('');

  const fetchEssay = () => {
    api
      .get<EssayResponse>(`/essays/${id}`)
      .then((res) => setEssay(res.data))
      .catch(() => setError('Failed to load essay'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchEssay();
  }, [id]);

  // Auto-refresh while grading
  useEffect(() => {
    if (essay?.status !== 'GRADING') return;
    const interval = setInterval(() => {
      api
        .get<EssayResponse>(`/essays/${id}`)
        .then((res) => setEssay(res.data));
    }, 3000);
    return () => clearInterval(interval);
  }, [essay?.status, id]);

  const handleGrade = async () => {
    setGrading(true);
    try {
      const res = await api.post<EssayResponse>(`/essays/${id}/grade`);
      setEssay(res.data);
    } catch {
      setError('Failed to start grading');
    } finally {
      setGrading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm('Are you sure you want to delete this essay?')) return;
    setDeleting(true);
    try {
      await api.delete(`/essays/${id}`);
      navigate('/essays');
    } catch {
      setError('Failed to delete essay');
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-8 h-8 text-primary-500 animate-spin" />
      </div>
    );
  }

  if (error && !essay) {
    return (
      <div className="flex items-center justify-center h-64 text-red-500 gap-2">
        <AlertCircle className="w-5 h-5" />
        {error}
      </div>
    );
  }

  if (!essay) return null;

  const canGrade = essay.status === 'SUBMITTED' || essay.status === 'FAILED';
  const isGrading = essay.status === 'GRADING';

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between gap-4">
        <div className="flex items-start gap-3">
          <button
            onClick={() => navigate('/essays')}
            className="mt-1 p-1.5 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{essay.title}</h1>
            <div className="flex items-center gap-3 mt-1.5">
              <StatusBadge status={essay.status} />
              <span className="text-sm text-gray-500">
                {new Date(essay.submittedAt).toLocaleString()}
              </span>
            </div>
          </div>
        </div>
        <div className="flex items-center gap-2">
          {canGrade && (
            <button
              onClick={handleGrade}
              disabled={grading}
              className="inline-flex items-center gap-2 px-4 py-2 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-lg transition text-sm disabled:opacity-50 cursor-pointer disabled:cursor-not-allowed"
            >
              {grading ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                <Sparkles className="w-4 h-4" />
              )}
              Grade with AI
            </button>
          )}
          <button
            onClick={handleDelete}
            disabled={deleting}
            className="inline-flex items-center gap-2 px-4 py-2 bg-red-50 hover:bg-red-100 text-red-700 font-medium rounded-lg transition text-sm disabled:opacity-50 cursor-pointer disabled:cursor-not-allowed"
          >
            {deleting ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : (
              <Trash2 className="w-4 h-4" />
            )}
            Delete
          </button>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg p-3">
          {error}
        </div>
      )}

      {/* Grading animation */}
      {isGrading && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-6 text-center">
          <RefreshCw className="w-8 h-8 text-yellow-600 animate-spin mx-auto mb-3" />
          <p className="font-medium text-yellow-800">
            AI is grading your essay...
          </p>
          <p className="text-sm text-yellow-600 mt-1">
            This usually takes a few seconds. The page will update automatically.
          </p>
        </div>
      )}

      {/* Score & Feedback */}
      {essay.status === 'GRADED' && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-white rounded-xl border border-gray-200 p-6 text-center">
            <p className="text-sm text-gray-500 mb-1">Score</p>
            <p className="text-4xl font-bold text-primary-600">
              {essay.score}
              <span className="text-lg text-gray-400">/100</span>
            </p>
          </div>
          <div className="md:col-span-2 bg-white rounded-xl border border-gray-200 p-6">
            <p className="text-sm font-medium text-gray-500 mb-2">
              AI Feedback
            </p>
            <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
              {essay.feedback}
            </p>
          </div>
        </div>
      )}

      {/* Essay content */}
      <div className="bg-white rounded-xl border border-gray-200 p-6">
        <h2 className="text-sm font-medium text-gray-500 mb-3">
          Essay Content
        </h2>
        <div className="prose max-w-none text-gray-700 whitespace-pre-wrap leading-relaxed">
          {essay.text}
        </div>
      </div>
    </div>
  );
}
