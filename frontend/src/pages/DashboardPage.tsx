import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import type { DashboardResponse } from '../types';
import StatusBadge from '../components/StatusBadge';
import type { EssayStatus } from '../types';
import {
  FileText,
  TrendingUp,
  Award,
  TrendingDown,
  Loader2,
  AlertCircle,
} from 'lucide-react';

export default function DashboardPage() {
  const [data, setData] = useState<DashboardResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .get<DashboardResponse>('/dashboard')
      .then((res) => setData(res.data))
      .catch(() => setError('Failed to load dashboard'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-8 h-8 text-primary-500 animate-spin" />
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="flex items-center justify-center h-64 text-red-500 gap-2">
        <AlertCircle className="w-5 h-5" />
        {error}
      </div>
    );
  }

  const statCards = [
    {
      label: 'Total Essays',
      value: data.totalEssays,
      icon: FileText,
      color: 'bg-blue-500',
    },
    {
      label: 'Average Score',
      value: data.averageScore != null ? data.averageScore.toFixed(1) : '-',
      icon: TrendingUp,
      color: 'bg-green-500',
    },
    {
      label: 'Highest Score',
      value: data.highestScore != null ? data.highestScore.toFixed(1) : '-',
      icon: Award,
      color: 'bg-purple-500',
    },
    {
      label: 'Lowest Score',
      value: data.lowestScore != null ? data.lowestScore.toFixed(1) : '-',
      icon: TrendingDown,
      color: 'bg-orange-500',
    },
  ];

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-500 mt-1">Overview of your essay activity</p>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {statCards.map(({ label, value, icon: Icon, color }) => (
          <div
            key={label}
            className="bg-white rounded-xl border border-gray-200 p-5 flex items-start gap-4"
          >
            <div className={`${color} p-2.5 rounded-lg text-white`}>
              <Icon className="w-5 h-5" />
            </div>
            <div>
              <p className="text-sm text-gray-500">{label}</p>
              <p className="text-2xl font-bold text-gray-900 mt-0.5">
                {value}
              </p>
            </div>
          </div>
        ))}
      </div>

      {/* Status breakdown */}
      {Object.keys(data.essaysByStatus).length > 0 && (
        <div className="bg-white rounded-xl border border-gray-200 p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            Essays by Status
          </h2>
          <div className="flex flex-wrap gap-6">
            {Object.entries(data.essaysByStatus).map(([status, count]) => (
              <div key={status} className="flex items-center gap-2">
                <StatusBadge status={status as EssayStatus} />
                <span className="text-lg font-semibold text-gray-900">
                  {count}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Recent essays */}
      <div className="bg-white rounded-xl border border-gray-200">
        <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">
            Recent Essays
          </h2>
          <Link
            to="/essays"
            className="text-sm text-primary-600 hover:text-primary-700 font-medium"
          >
            View all
          </Link>
        </div>
        {data.recentEssays.length === 0 ? (
          <div className="p-8 text-center text-gray-500">
            <FileText className="w-10 h-10 mx-auto mb-3 text-gray-300" />
            <p>No essays yet. Submit your first essay!</p>
            <Link
              to="/essays/new"
              className="inline-block mt-3 text-primary-600 hover:text-primary-700 font-medium text-sm"
            >
              Create Essay
            </Link>
          </div>
        ) : (
          <div className="divide-y divide-gray-100">
            {data.recentEssays.map((essay) => (
              <Link
                key={essay.id}
                to={`/essays/${essay.id}`}
                className="flex items-center justify-between px-6 py-4 hover:bg-gray-50 transition-colors"
              >
                <div className="min-w-0 flex-1">
                  <p className="font-medium text-gray-900 truncate">
                    {essay.title}
                  </p>
                  <p className="text-sm text-gray-500 mt-0.5">
                    {new Date(essay.submittedAt).toLocaleDateString()}
                  </p>
                </div>
                <div className="flex items-center gap-3 ml-4">
                  {essay.score != null && (
                    <span className="text-sm font-semibold text-gray-900">
                      {essay.score}/100
                    </span>
                  )}
                  <StatusBadge status={essay.status} />
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
