import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import type { EssayResponse } from '../types';
import { ArrowLeft, Loader2 } from 'lucide-react';

export default function CreateEssayPage() {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [text, setText] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (text.length < 50) {
      setError('Essay text must be at least 50 characters.');
      return;
    }
    setError('');
    setLoading(true);
    try {
      const res = await api.post<EssayResponse>('/essays', { title, text });
      navigate(`/essays/${res.data.id}`);
    } catch (err: any) {
      setError(
        err.response?.data?.message || 'Failed to create essay. Please try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center gap-3">
        <button
          onClick={() => navigate('/essays')}
          className="p-1.5 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition cursor-pointer"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">New Essay</h1>
          <p className="text-gray-500 mt-0.5">
            Submit your essay for AI grading
          </p>
        </div>
      </div>

      <form
        onSubmit={handleSubmit}
        className="bg-white rounded-xl border border-gray-200 p-6 space-y-5"
      >
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg p-3">
            {error}
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Title
          </label>
          <input
            type="text"
            required
            maxLength={200}
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full px-3 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none transition text-sm"
            placeholder="Enter essay title"
          />
        </div>

        <div>
          <div className="flex items-center justify-between mb-1">
            <label className="block text-sm font-medium text-gray-700">
              Essay Text
            </label>
            <span
              className={`text-xs ${
                text.length < 50 ? 'text-red-500' : 'text-gray-400'
              }`}
            >
              {text.length} characters (min 50)
            </span>
          </div>
          <textarea
            required
            rows={16}
            value={text}
            onChange={(e) => setText(e.target.value)}
            className="w-full px-3 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 outline-none transition text-sm resize-y"
            placeholder="Write your essay here..."
          />
        </div>

        <div className="flex items-center justify-end gap-3 pt-2">
          <button
            type="button"
            onClick={() => navigate('/essays')}
            className="px-4 py-2.5 text-gray-700 hover:bg-gray-100 font-medium rounded-lg transition text-sm cursor-pointer"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className="inline-flex items-center gap-2 px-6 py-2.5 bg-primary-600 hover:bg-primary-700 text-white font-medium rounded-lg transition text-sm disabled:opacity-50 cursor-pointer disabled:cursor-not-allowed"
          >
            {loading && <Loader2 className="w-4 h-4 animate-spin" />}
            Submit Essay
          </button>
        </div>
      </form>
    </div>
  );
}
