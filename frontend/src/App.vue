<template>
  <v-app>
    <v-app-bar color="teal-darken-4" density="comfortable" flat>
      <v-app-bar-title>Pedidos e Cozinha</v-app-bar-title>
      <template #append>
        <v-chip variant="tonal" color="white" class="text-teal-darken-4">
          Vue + Vuetify + Microsserviço
        </v-chip>
      </template>
    </v-app-bar>

    <v-main>
      <v-container class="pa-4 pa-md-8 app-fundo">
        <v-row dense>
          <v-col cols="12" lg="7">
            <v-card elevation="12" rounded="xl" class="pa-4 pa-md-6 card-principal h-100">
              <v-row align="center" class="mb-4">
                <v-col cols="12" md="8">
                  <v-card-title class="text-h4 font-weight-bold pa-0">Pedidos</v-card-title>
                  <v-card-subtitle class="pa-0 mt-2">
                    Cadastre pedidos e acompanhe o estado geral da aplicação.
                  </v-card-subtitle>
                </v-col>

                <v-col cols="12" md="4" class="text-md-right">
                  <v-chip color="teal-darken-2" variant="flat" class="ma-1">
                    {{ pedidos.length }} pedidos
                  </v-chip>
                  <v-chip color="amber-darken-2" variant="flat" class="ma-1">
                    {{ pedidosPendentes.length }} pendentes
                  </v-chip>
                </v-col>
              </v-row>

              <v-alert
                v-if="mensagemPedidos"
                :type="tipoMensagemPedidos"
                variant="tonal"
                class="mb-4"
                border="start"
              >
                {{ mensagemPedidos }}
              </v-alert>

              <v-form @submit.prevent="criarPedido" class="mb-4">
                <v-row dense>
                  <v-col cols="12" md="9">
                    <v-text-field
                      v-model="descricao"
                      label="Descrição do pedido"
                      placeholder="Ex.: 2 hambúrgueres e 1 refrigerante"
                      variant="outlined"
                      density="comfortable"
                      prepend-inner-icon="mdi-receipt-text-plus"
                      :disabled="carregandoPedidos"
                      hide-details="auto"
                    />
                  </v-col>

                  <v-col cols="12" md="3" class="d-flex align-center">
                    <v-btn
                      type="submit"
                      color="teal-darken-2"
                      size="large"
                      block
                      :loading="carregandoPedidos"
                      :disabled="carregandoPedidos"
                    >
                      Adicionar
                    </v-btn>
                  </v-col>
                </v-row>
              </v-form>

              <v-progress-linear
                v-if="carregandoPedidos"
                indeterminate
                color="teal-darken-2"
                rounded
                class="mb-4"
              />

              <v-divider class="mb-4" />

              <v-list v-if="pedidos.length > 0" lines="two" class="bg-transparent pa-0">
                <v-list-item
                  v-for="pedido in pedidos"
                  :key="pedido.id"
                  rounded="lg"
                  class="mb-3 pedido-item"
                  :class="pedido.finalizado ? 'pedido-finalizado' : 'pedido-pendente'"
                >
                  <template #prepend>
                    <v-avatar :color="pedido.finalizado ? 'green-darken-2' : 'orange-darken-2'" size="40">
                      <v-icon icon="mdi-food" color="white" />
                    </v-avatar>
                  </template>

                  <template #title>
                    <div class="d-flex flex-wrap align-center ga-2">
                      <span :class="pedido.finalizado ? 'texto-finalizado' : ''">{{ pedido.descricao }}</span>
                      <v-chip size="small" :color="chipStatus(preparoDoPedido(pedido.id)?.status || 'RECEBIDO')" variant="flat">
                        {{ textoStatus(preparoDoPedido(pedido.id)?.status || 'RECEBIDO') }}
                      </v-chip>
                    </div>
                  </template>

                  <template #subtitle>
                    <span v-if="pedido.finalizado" class="text-green-darken-2">Pedido finalizado no sistema principal</span>
                    <span v-else class="text-orange-darken-2">Pedido ativo</span>
                  </template>

                  <template #append>
                    <v-btn
                      v-if="!pedido.finalizado"
                      color="teal-darken-2"
                      variant="flat"
                      :loading="pedidoEmAcao === pedido.id"
                      @click="finalizarPedido(pedido.id)"
                    >
                      Finalizar
                    </v-btn>
                  </template>
                </v-list-item>
              </v-list>

              <v-sheet v-else rounded="lg" class="pa-6 text-center vazio-caixa">
                <v-icon icon="mdi-clipboard-text-outline" size="48" color="teal-darken-2" class="mb-3" />
                <div class="text-h6 mb-1">Nenhum pedido cadastrado</div>
                <div class="text-body-2 text-medium-emphasis">
                  Adicione o primeiro pedido usando o formulário acima.
                </div>
              </v-sheet>
            </v-card>
          </v-col>

          <v-col cols="12" lg="5">
            <v-card elevation="12" rounded="xl" class="pa-4 pa-md-6 card-principal h-100">
              <v-card-title class="text-h5 font-weight-bold pa-0">Microsserviço de preparo</v-card-title>
              <v-card-subtitle class="pa-0 mt-2 mb-4">
                Atualize o andamento dos pedidos em um serviço separado.
              </v-card-subtitle>

              <v-alert
                v-if="mensagemPreparos"
                :type="tipoMensagemPreparos"
                variant="tonal"
                class="mb-4"
                border="start"
              >
                {{ mensagemPreparos }}
              </v-alert>

              <v-progress-linear
                v-if="carregandoPreparos"
                indeterminate
                color="amber-darken-2"
                rounded
                class="mb-4"
              />

              <v-list v-if="preparos.length > 0" class="bg-transparent pa-0">
                <v-list-item
                  v-for="preparo in preparos"
                  :key="preparo.pedidoId"
                  class="mb-3 preparo-item"
                  rounded="lg"
                >
                  <template #prepend>
                    <v-avatar :color="chipStatus(preparo.status)" size="40">
                      <v-icon icon="mdi-chef-hat" color="white" />
                    </v-avatar>
                  </template>

                  <template #title>
                    Pedido #{{ preparo.pedidoId }} - {{ preparo.descricao }}
                  </template>

                  <template #subtitle>
                    <div class="d-flex flex-column ga-1">
                      <v-chip size="small" :color="chipStatus(preparo.status)" variant="flat" class="align-self-start">
                        {{ textoStatus(preparo.status) }}
                      </v-chip>
                      <span class="text-body-2 text-medium-emphasis">
                        Atualizado em {{ formatarData(preparo.atualizadoEm) }}
                      </span>
                    </div>
                  </template>

                  <template #append>
                    <div class="d-flex flex-column ga-2 status-controles">
                      <v-select
                        v-model="statusSelecionados[preparo.pedidoId]"
                        :items="statusOpcoes"
                        item-title="titulo"
                        item-value="valor"
                        label="Status"
                        variant="outlined"
                        density="compact"
                        hide-details
                        :disabled="carregandoPreparos"
                      />

                      <v-btn
                        color="amber-darken-2"
                        variant="flat"
                        :loading="pedidoEmAcaoPreparo === preparo.pedidoId"
                        @click="atualizarStatusPreparo(preparo.pedidoId)"
                      >
                        Atualizar
                      </v-btn>
                    </div>
                  </template>
                </v-list-item>
              </v-list>

              <v-sheet v-else rounded="lg" class="pa-6 text-center vazio-caixa">
                <v-icon icon="mdi-robot" size="48" color="amber-darken-2" class="mb-3" />
                <div class="text-h6 mb-1">Nenhum item no preparo</div>
                <div class="text-body-2 text-medium-emphasis">
                  Quando um pedido for criado, ele aparece aqui vindo do microsserviço.
                </div>
              </v-sheet>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';

const pedidos = ref([]);
const preparos = ref([]);
const descricao = ref('');

const mensagemPedidos = ref('');
const tipoMensagemPedidos = ref('success');
const mensagemPreparos = ref('');
const tipoMensagemPreparos = ref('success');

const carregandoPedidos = ref(false);
const carregandoPreparos = ref(false);
const pedidoEmAcao = ref(null);
const pedidoEmAcaoPreparo = ref(null);

const statusSelecionados = reactive({});

const statusOpcoes = [
  { titulo: 'Recebido', valor: 'RECEBIDO' },
  { titulo: 'Em preparo', valor: 'EM_PREPARO' },
  { titulo: 'Pronto', valor: 'PRONTO' },
  { titulo: 'Entregue', valor: 'ENTREGUE' }
];

const basesApi = ['', 'http://localhost:8081'];

const pedidosPendentes = computed(() => pedidos.value.filter((pedido) => !pedido.finalizado));

function textoStatus(status) {
  const item = statusOpcoes.find((opcao) => opcao.valor === status);
  return item ? item.titulo : 'Recebido';
}

function chipStatus(status) {
  switch (status) {
    case 'EM_PREPARO':
      return 'amber-darken-2';
    case 'PRONTO':
      return 'green-darken-2';
    case 'ENTREGUE':
      return 'blue-darken-2';
    default:
      return 'teal-darken-2';
  }
}

function formatarData(valor) {
  return new Date(valor).toLocaleString('pt-BR');
}

function preparoDoPedido(pedidoId) {
  return preparos.value.find((preparo) => preparo.pedidoId === pedidoId);
}

async function chamarApi(endpoint, opcoes = {}) {
  let ultimoErro = null;

  for (const base of basesApi) {
    try {
      const resposta = await fetch(`${base}${endpoint}`, opcoes);

      if (resposta.ok || resposta.status < 500) {
        return resposta;
      }

      ultimoErro = new Error(`Resposta inválida da API: ${resposta.status}`);
    } catch (erro) {
      ultimoErro = erro;
    }
  }

  throw ultimoErro ?? new Error('Falha ao conectar na API.');
}

async function carregarPedidos() {
  const resposta = await chamarApi('/api/pedidos');

  if (!resposta.ok) {
    throw new Error('Falha ao carregar os pedidos.');
  }

  pedidos.value = await resposta.json();
}

async function carregarPreparos() {
  const resposta = await chamarApi('/api/preparos');

  if (!resposta.ok) {
    throw new Error('Falha ao carregar o microsserviço de preparo.');
  }

  preparos.value = await resposta.json();

  preparos.value.forEach((preparo) => {
    statusSelecionados[preparo.pedidoId] = preparo.status;
  });
}

async function criarPedido() {
  if (!descricao.value.trim()) {
    tipoMensagemPedidos.value = 'warning';
    mensagemPedidos.value = 'Digite a descrição do pedido.';
    return;
  }

  carregandoPedidos.value = true;
  mensagemPedidos.value = '';

  try {
    const resposta = await chamarApi('/api/pedidos', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ descricao: descricao.value.trim() })
    });

    if (!resposta.ok) {
      throw new Error('Não foi possível criar o pedido.');
    }

    descricao.value = '';
    tipoMensagemPedidos.value = 'success';
    mensagemPedidos.value = 'Pedido criado com sucesso.';
    await Promise.all([carregarPedidos(), carregarPreparos()]);
  } catch (erro) {
    tipoMensagemPedidos.value = 'error';
    mensagemPedidos.value = erro.message;
  } finally {
    carregandoPedidos.value = false;
  }
}

async function finalizarPedido(id) {
  pedidoEmAcao.value = id;
  mensagemPedidos.value = '';

  try {
    const resposta = await chamarApi(`/api/pedidos/${id}/finalizar`, {
      method: 'PATCH'
    });

    if (!resposta.ok) {
      throw new Error('Não foi possível finalizar o pedido.');
    }

    tipoMensagemPedidos.value = 'success';
    mensagemPedidos.value = 'Pedido finalizado.';
    await Promise.all([carregarPedidos(), carregarPreparos()]);
  } catch (erro) {
    tipoMensagemPedidos.value = 'error';
    mensagemPedidos.value = erro.message;
  } finally {
    pedidoEmAcao.value = null;
  }
}

async function atualizarStatusPreparo(pedidoId) {
  pedidoEmAcaoPreparo.value = pedidoId;
  mensagemPreparos.value = '';

  try {
    const status = statusSelecionados[pedidoId];

    const resposta = await chamarApi(`/api/pedidos/${pedidoId}/preparo`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ status })
    });

    if (!resposta.ok) {
      throw new Error('Não foi possível atualizar o status do preparo.');
    }

    tipoMensagemPreparos.value = 'success';
    mensagemPreparos.value = 'Status do preparo atualizado.';
    await carregarPreparos();
  } catch (erro) {
    tipoMensagemPreparos.value = 'error';
    mensagemPreparos.value = erro.message;
  } finally {
    pedidoEmAcaoPreparo.value = null;
  }
}

onMounted(async () => {
  carregandoPedidos.value = true;
  carregandoPreparos.value = true;

  try {
    await Promise.all([carregarPedidos(), carregarPreparos()]);
  } catch (erro) {
    tipoMensagemPedidos.value = 'error';
    mensagemPedidos.value = erro.message;
    tipoMensagemPreparos.value = 'error';
    mensagemPreparos.value = erro.message;
  } finally {
    carregandoPedidos.value = false;
    carregandoPreparos.value = false;
  }
});
</script>

<style scoped>
.app-fundo {
  min-height: calc(100vh - 64px);
  background:
    radial-gradient(circle at top left, rgba(13, 148, 136, 0.12), transparent 28%),
    linear-gradient(180deg, #f8fafc 0%, #eef2ff 100%);
}

.card-principal {
  border: 1px solid rgba(15, 118, 110, 0.12);
}

.h-100 {
  height: 100%;
}

.pedido-item,
.preparo-item {
  border: 1px solid rgba(15, 118, 110, 0.12);
}

.pedido-pendente {
  background: linear-gradient(90deg, rgba(255, 247, 237, 1) 0%, rgba(255, 255, 255, 1) 24%);
}

.pedido-finalizado {
  background: linear-gradient(90deg, rgba(236, 253, 245, 1) 0%, rgba(255, 255, 255, 1) 24%);
}

.vazio-caixa {
  border: 1px dashed rgba(15, 118, 110, 0.2);
  background: rgba(255, 255, 255, 0.7);
}

.texto-finalizado {
  text-decoration: line-through;
  color: #64748b;
}

.status-controles {
  min-width: 180px;
  max-width: 220px;
}
</style>
