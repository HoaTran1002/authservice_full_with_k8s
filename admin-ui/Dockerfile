FROM node:20-alpine AS build
WORKDIR /app
COPY package.json vite.config.js ./
COPY index.html ./
COPY src ./src
RUN npm install
RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /app
COPY --from=build /app/dist ./dist
RUN npm install vite
EXPOSE 3000
CMD ["npx","vite","preview","--port","3000","--host"]